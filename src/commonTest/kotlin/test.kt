import action.*
import com.soywiz.klock.*
import com.soywiz.korge.input.*
import com.soywiz.korge.tests.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import templates.Kobold
import templates.Register
import time.GlobalTimer
import action.roles.IInstantiator.Companion.Instantiate
import action.roles.IInstantiator.Companion.instantiateParamList
import com.soywiz.korio.async.runBlockingNoJs
import templates.Cave
import kotlin.test.*
import kotlin.time.ExperimentalTime

class MyTest : ViewsForTesting() {

	@ExperimentalCoroutinesApi
	@ExperimentalUnsignedTypes
	@ExperimentalTime
	@Test
	fun testPerform() = runBlockingNoJs {
		val globalReg = Register(kInstanceName = "testGlobalRegister")

		Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Kobold, "gragg", globalReg) )
		Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Kobold, "rrawwr", globalReg) )
		Action.Immediate.execute(action = Instantiate, actionParamList = Instantiate.instantiateParamList(Cave, "spookyCave", globalReg) )
		GlobalTimer.perform(globalRegister = globalReg)

		return@runBlockingNoJs
	}
}