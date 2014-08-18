import org.zeromq.ZMQ
import org.zeromq.ZMQ.Poller

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

  def doQuit() = {
    val quit = ctx.socket(ZMQ.REQ)
    quit connect QUIT
    quit.send("".getBytes(), ZMQ.NOBLOCK)
    quit.close()
  }

  def runCycle = {
    var cmd = 1
    val poller = ctx.poller()
    poller.setTimeout(500)
    poller.register(input, Poller.POLLIN)
    poller.register(command, Poller.POLLOUT)
    poller.register(quit, Poller.POLLIN)
    do {
      if (poller.pollin(0)) {
        val bytes = input.recv(0)
        println(new String(bytes, "UTF-8"))

        if (poller.pollout(1)) {
          command.send(s"cmd #$cmd".getBytes(), ZMQ.NOBLOCK)
          cmd += 1
        }
      }
      if (System.in.read() != -1) {
        doQuit()
      }
      poller.poll()
    } while (!poller.pollin(2))
  }

  println("Running main cycle press any key to quit...")
  runCycle

  command.close()
  quit.close()
  input.close()
  ctx.term()
}