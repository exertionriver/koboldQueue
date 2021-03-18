import action.Action
import action.actions.Instantiate
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
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.InstantiateParamList(Cave, "spookyCave", globalReg).actionParamList() )
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.InstantiateParamList(Kobold, "gragg", globalReg).actionParamList() )
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.InstantiateParamList(Kobold, "rrawwr", globalReg).actionParamList() )

}