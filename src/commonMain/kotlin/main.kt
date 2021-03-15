import actions.Action
import actions.actionables.IInstantiator.Companion.Instantiate
import actions.actionables.IInstantiator.Companion.instantiateParamList
import com.soywiz.korge.*
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import templates.Cave
import templates.Kobold
import templates.Register
import time.GlobalTimer
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {

	val globalReg = Register(kInstanceName = "testGlobalRegister")
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Kobold, "gragg", globalReg) )
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Kobold, "rrawwr", globalReg) )
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Cave, "spookyCave", globalReg) )

	GlobalTimer.perform(globalReg)

}