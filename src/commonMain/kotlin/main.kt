import com.soywiz.korge.*
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import render.RenderActionPlex
import templates.Cave
import templates.Kobold
import templates.Register
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
suspend fun main() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

	RenderActionPlex.container = containerRoot
//	GlobalChannel.initLogInfoChannel()

	val globalReg = Register(kInstanceName = "testGlobalRegister")

	instantiate { template = Cave; kInstanceName = "spookyCave"; register = globalReg }
	instantiate { template = Kobold; kInstanceName = "gragg"; register = globalReg }
	instantiate { template = Kobold; kInstanceName = "rrawwr"; register = globalReg }

}