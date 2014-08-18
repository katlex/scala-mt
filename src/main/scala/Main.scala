import java.text.SimpleDateFormat
import org.zeromq.ZMQ
import org.zeromq.ZMQ.Poller
import scala.concurrent.duration._
import com.katlex.utils._

object Main extends App {

  println("Welcome to Scala MQL4ZMQ demo")
  println(s"ZeroMQ version: ${ZMQ.getVersionString()}")

  lazy val QUIT = "inproc://quit"
  def localhost(port:Int) = s"tcp://localhost:$port"

  val ctx = ZMQ.context(1)
  val input = ctx.socket(ZMQ.SUB)
  val quit = ctx.socket(ZMQ.REP)
  val command = ctx.socket(ZMQ.PUB)

  quit bind QUIT
  input connect localhost(2027)
  command connect localhost(2028)

  input.subscribe("tick".getBytes())

  def poller = {
    val p = ctx.poller()
    p setTimeout (1 second).toMicros
    p
  }

  def doQuit() = {
    val quit = ctx.socket(ZMQ.REQ)
    quit connect QUIT
    quit.send("".getBytes(), ZMQ.NOBLOCK)
    quit.close()
  }

  def runCycle = {
    var cmd = 1
    val dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm")
    val inPoller = poller
    inPoller.register(input, Poller.POLLIN)
    inPoller.register(quit, Poller.POLLIN)
    val outPoller = poller
    outPoller.register(command, Poller.POLLOUT)

    do {
      if (inPoller.pollin(0)) {
        val bytes = input.recv(0)
        val data = new String(bytes, "UTF-8").split(" ").toList match {
          case "tick" :: Unapply.BigDecimal(bid) :: Unapply.BigDecimal(ask) :: Unapply.Date(date) :: Nil =>
            println (s"${dateFormat.format(date)} Ask: $ask Bid: $bid Spread: ${ask - bid}")
        }

        outPoller.poll()
        if (outPoller.pollout(0)) {
          command.send(s"cmd #$cmd".getBytes(), ZMQ.NOBLOCK)
          cmd += 1
        }
      }
      inPoller.poll()
    } while (!inPoller.pollin(1))
  }

  new Thread() {
    override def run = {
      if (System.in.read() != -1) {
        doQuit()
      }
      Thread.sleep(500)
    }
    setDaemon(true)
  } .start()

  println("Running main cycle press any key to quit...")
  runCycle

  command.close()
  quit.close()
  input.close()
  ctx.term()
}