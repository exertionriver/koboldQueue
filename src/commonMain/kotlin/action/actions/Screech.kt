package action.actions

import ParamList
import action.Action
import action.ActionPriority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import param
import templates.IInstance
import templates.Register
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
object Screech : Action(actionLabel = "screech"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = ScreechParamList().screechDescription()
    , executor = fun (screechParams : ParamList?) : String {
        if (screechParams == null) return ScreechParamList().screechDescription()

//        println ("screeching!")

        val screechObjects = ScreechParamList(screechParams).register!!.entries
            .filterKeys { (it != ScreechParamList(screechParams).kInstance) }

//        println ( "entries: ${ScreechParamList(screechParams).register!!.entries}")
//        println ( "objs: ${screechObjects.keys}")

        screechObjects.forEach { it.key.interrupted = true; }//println ("interrupting ${it.key.getInstanceName()}") }

        return if (!screechObjects.isNullOrEmpty() )
            ScreechParamList(screechParams).screechDescription().plus(": " +
                    screechObjects.map{ it.key.getInstanceName() }
                        .reduce{ lookResult : String, element -> lookResult.plus(" $element") }).plus (" affected")
        else
            ScreechParamList(screechParams).screechDescription()
        }
    ) {
        class ScreechParamList(var kInstance: IInstance?, var register: Register?) {

            constructor(actionParamList: ParamList) : this(
                kInstance = actionParamList.param<IInstance>(0)
                , register = actionParamList.param<Register>(1)
            )

            constructor() : this(kInstance = null, register = null)

            fun screechDescription() : String = "${Screech::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} screeches randomly " +
                "in Register ${registerOrT()}"

            private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

            private fun registerOrT() = register?.getInstanceName() ?: Register::class.simpleName

            @Suppress("UNCHECKED_CAST")
            fun actionParamList() = listOf(kInstance, register) as ParamList
        }

    fun params(lambda: ScreechParamList.() -> Unit) = ScreechParamList().apply(lambda).actionParamList()
}