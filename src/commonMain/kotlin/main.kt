import actions.Action
import actions.actionables.IInstantiator.Companion.Instantiate
import actions.actionables.IInstantiator.Companion.instantiateParamList
import com.soywiz.korge.*
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import render.RenderActionPlex
import templates.Cave
import templates.Kobold
import templates.Register
import time.GlobalTimer
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
suspend fun main() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

	RenderActionPlex.container = containerRoot

	val globalReg = Register(kInstanceName = "testGlobalRegister")
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Cave, "spookyCave", globalReg) )
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Kobold, "gragg", globalReg) )
	Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Kobold, "rrawwr", globalReg) )

	//GlobalTimer.perform(globalReg)
}