package render

import RenderInstancePositionMap
import RenderInstanceViewMap
import action.ActionPriority.Companion.BaseAction
import com.soywiz.korge.internal.KorgeInternal
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.paint.LinearGradientPaint
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.*
import state.ActionState
import state.ActionState.Companion.ActionExecute
import state.ActionState.Companion.ActionPrepare
import state.ActionState.Companion.ActionRecover
import action.StateAction
import action.actions.*
import templates.IInstance
import time.Moment
import time.Timer
import kotlin.time.ExperimentalTime

object RenderActionPlex {

    @ExperimentalUnsignedTypes
    val instances: RenderInstancePositionMap = mutableMapOf()
    @ExperimentalUnsignedTypes
    val currentInstanceViews: RenderInstanceViewMap = mutableMapOf()

    lateinit var container: Container //set this with Korge.rootContainer before running anything

    val font = BitmapFont(
        DefaultTtfFont, 64.0,
        paint = LinearGradientPaint(0, 0, 0, 50).add(0.0, Colors.CADETBLUE).add(1.0, Colors.PURPLE),
//        effect = BitmapEffect(
//            dropShadowX = 2,
//            dropShadowY = 2,
//            dropShadowRadius = 2,
//            dropShadowColor = Colors["#5f005f"]
//        )
    )

    fun Container.clear(xPos: Double, yPos: Double) : View {
        val checkTimer = Timer()

        return this.solidRect(width = 150, height = 300, color = Colors["#2b2b2b"]).position(xPos, yPos)

//        println("RenderActionPlex(clear) @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()} $momentCounter")

    }

    @ExperimentalUnsignedTypes
    fun getOpenPosition(): Int {
        var curPosIdx = 0
        val sizeIdx = instances.size - 1

        while (curPosIdx <= sizeIdx) {

            if (instances[curPosIdx] == null) return curPosIdx

            curPosIdx++
        }

        return instances.size
    }

    @KorgeInternal
    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance: IInstance) {

        val startingPosition = Point(50, 50)


        currentInstanceViews.filterValues { it == kInstance.getInstanceId()}.keys.forEach { if (container.children.contains(it)) container.removeChild(it) }
        currentInstanceViews.filterValues { it == kInstance.getInstanceId() }.keys.forEach { currentInstanceViews.remove(it) }

        instances.toMap().forEach { pos ->
            val xPos = startingPosition.x + (pos.key % 6) * 150
            val yPos = startingPosition.y + (pos.key / 6) * 300

            if (pos.value == kInstance) container.clear(xPos - 25, yPos - 25)
        }

        instances.remove(instances.filterValues { it == kInstance }.keys.toList()[0])
    }

    var momentCounter = 0


    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    suspend fun renderInstance(instanceId : UUID, instanceName : String, instanceLocation : Int, instanceMoment: Moment, actionPlexMap: Map<UUID, StateAction>, startingPosition: Point, interrupted : Boolean) = coroutineScope {

        val checkTimer = Timer()

        val instanceViews: RenderInstanceViewMap = mutableMapOf()

        val renderPosition = Point(x = startingPosition.x + (instanceLocation % 6) * 150, y = startingPosition.y + (instanceLocation / 6) * 300)

        instanceViews.put(container.clear(renderPosition.x - 25, renderPosition.y - 25), instanceId)

        instanceViews.put(container.text(
            instanceName,
            font = font,
            textSize = 24.0,
            alignment = TextAlignment.BASELINE_LEFT
        ).position(renderPosition.x, renderPosition.y), instanceId)

        var slotIdx = 0

        // val instancePlex = state(pos.value.actionPlex)

        //instancePlex.collect
        actionPlexMap.forEach { slot ->

            (1..slot.value.plexSlotsFilled).toList().forEach {
                instanceViews.putAll(renderSlot(instanceId, slotIdx, slot.value, instanceMoment, renderPosition, interrupted))
                slotIdx++
            }

        }
//        println("RenderActionPlex(renderInstance) @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()} $momentCounter")

        return@coroutineScope instanceViews
    }

    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun renderSlot(instanceId: UUID, slotIdx: Int, stateAction: StateAction, instanceMoment : Moment, renderPos: Point, interrupted : Boolean) = coroutineScope {

//        val checkTimer = Timer()

        val instanceViews: RenderInstanceViewMap = mutableMapOf()

        val momentsElapsed = (stateAction.timer.getMillisecondsElapsed() / instanceMoment.milliseconds).toDouble()

        val percentFilled = when (stateAction.actionState) {
            ActionPrepare -> (momentsElapsed + 1) / stateAction.action.momentsToPrepare.toDouble()
            ActionExecute -> (momentsElapsed + 1) / stateAction.action.momentsToExecute.toDouble()
            ActionRecover -> (momentsElapsed + 1) / stateAction.action.momentsToRecover.toDouble()
            else -> momentsElapsed / (momentsElapsed + 1) //Zeno's queue
        }

        if (percentFilled > 1) println("percentFilled overflow : $percentFilled for moment ${instanceMoment.milliseconds} $stateAction")

        val renderPercentFilled = if (percentFilled > 1) 1.0 else percentFilled

        //println ("momentsElapsed: $momentsElapsed -> percentFilled: $percentFilled")

        val xSlotPos = renderPos.x
        val ySlotPos = renderPos.y + 25 + slotIdx * 25
        val fillText = stateAction.action.actionLabel

        val fillTextColor = when (interrupted) {
            true -> Colors["#171717"]
            false -> when (stateAction.action) {
                Instantiate -> Colors["#37f585"]
                Destantiate -> Colors["#f58858"]
                Look -> Colors["#b9c3ff"]
                Watch -> Colors["#7978ff"]
                Reflect -> Colors["#4542ff"]
                Idle -> Colors["#f4ff1c"]
                else -> Colors["#f4ff1c"]
            }
        }

        val fillColor = when (interrupted) {
            true -> Colors["#eaeaea"]
            false -> when (stateAction.actionPriority) {
                BaseAction -> when (stateAction.actionState) {
                    ActionState.ActionPrepare -> Colors["#006c00"]
                    ActionState.ActionExecute -> Colors["#080a6c"]
                    ActionState.ActionRecover -> Colors["#6c0604"]
                    else -> Colors["#434241"]
                }
                else -> when (stateAction.actionState) {
                    ActionState.ActionPrepare -> Colors["#00db00"]
                    ActionState.ActionExecute -> Colors["#100be0"]
                    ActionState.ActionRecover -> Colors["#e00508"]
                    else -> Colors["#727170"]
                }
            }
        }

        instanceViews.put(container.roundRect(80, 20, 1, 1, fillColor.withA(80)).position(xSlotPos, ySlotPos), instanceId)
        instanceViews.put(container.roundRect((80 * renderPercentFilled).toInt(), 20, 1, 1, fillColor).position(xSlotPos, ySlotPos), instanceId)
        instanceViews.put(container.text(fillText, textSize = 14.0, color = fillTextColor).position(xSlotPos, ySlotPos), instanceId)

  //      println("RenderActionPlex(renderSlot) @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()} $momentCounter")

        return@coroutineScope instanceViews
    }

@KorgeInternal
@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun render(instanceId : UUID, instanceMoment: Moment, actionPlexMap: Map<UUID, StateAction>, interrupted : Boolean = false) = coroutineScope {

      //  println("container:$container, numChildren:${container.numChildren}, currentInstanceViews: ${currentInstanceViews.size}, children: ${container.children}")

  //      println("currentThreadId : ${com.soywiz.korio.lang.currentThreadId}")

        val checkTimer = Timer()

        val containerCullViewCount = container.numChildren - 500

        if (containerCullViewCount > 0) {
            for (idx in 0..containerCullViewCount) {
                if (container.firstChild != null) container.firstChild!!.removeFromParent()
            }
        }

        val instanceViews: RenderInstanceViewMap = mutableMapOf()

        val startingPosition = Point(50, 50)

        val renderInstanceEntry : Map.Entry<Int, IInstance>? = instances.filterValues { it.getInstanceId() == instanceId }.entries.firstOrNull()

        if (renderInstanceEntry == null) {
            println("RenderActionPlex.perform() instance not found for uuid $instanceId")
            return@coroutineScope Timer()
        }

   //     println("render @ ${ DateTime.now() } ${renderInstanceEntry.value.getInstanceName()} on $container")

        instanceViews.putAll(
                renderInstance(
                instanceId = instanceId,
                instanceName = renderInstanceEntry.value.getInstanceName(),
                instanceLocation = renderInstanceEntry.key,
                instanceMoment = instanceMoment,
                actionPlexMap = actionPlexMap,
                startingPosition = startingPosition,
                interrupted = interrupted
            )
        )

//    launch(views().coroutineContext) {
 //       val removePastViewsIter = instanceViews.iterator()
    //    while (removePastViewsIter.hasNext()) {
      //      GlobalChannel.viewRemoveChannel.send(removePastViewsIter.next().key)

//                if (container.children.toList().contains(entry.key)) container.removeChild(entry.key)
//            if (container.children.toList()
//                    .contains(entry.key)
//            ) entry.key.removeFromParent() //recommendation from soywiz
   //     }
//            pastInstanceViews.keys.forEach { if (container.children.contains(it)) container.removeChild(it) }

      //      println("RenderActionPlex @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()}")
        }
}
