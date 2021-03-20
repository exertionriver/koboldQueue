import action.Action
import action.StateAction
import action.actions.Destantiate
import action.actions.Instantiate
import com.soywiz.korge.view.View
import com.soywiz.korio.util.UUID
import condition.Condition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import templates.IInstance
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun instantiate(lambda: Instantiate.InstantiateParamList.() -> Unit) = Action.Immediate.execute( action = Instantiate, actionParamList = Instantiate.InstantiateParamList()
    .apply(lambda).actionParamList() )

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun destantiate(lambda: Destantiate.DestantiateParamList.() -> Unit) = Action.Immediate.execute( action = Destantiate, actionParamList = Destantiate.DestantiateParamList()
    .apply(lambda).actionParamList() )

inline fun <reified T> ParamList.param(index : Int) : T = if (this[index] is T) this[index] as T else throw IllegalArgumentException(this.toString())

typealias ParamList = List<Any>
typealias ActionConditionsMap = Map<Action, ConditionList?>

typealias ActionDescription = () -> String
typealias ActionExecutor = (actionParams : ParamList?) -> String?
typealias ConditionDescription = () -> String
typealias ConditionEvaluator = (conditionParams : ParamList?) -> Boolean?

@ExperimentalUnsignedTypes
typealias ActionPlex = MutableMap<UUID, StateAction> //slots to StateActions, max of maxPlexSize

typealias ImActionPlex = Map<UUID, StateAction> //slots to StateActions, max of maxPlexSize

typealias RegisterEntries = MutableMap<IInstance, Job>

typealias ImRegisterEntries = Map<IInstance, Job>

typealias RegisterData = Pair<UUID, ImRegisterEntries>

@ExperimentalCoroutinesApi
val registerChannel = Channel<RegisterData>(32)

@ExperimentalUnsignedTypes
typealias RenderInstancePositionMap = MutableMap<Int, IInstance>

@ExperimentalUnsignedTypes
typealias RenderInstanceViewMap = MutableMap<View, UUID>

typealias ConditionList = List<Condition>
typealias ConditionParamMap = Map<Condition, ParamList?>

