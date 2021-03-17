package render

import actions.ActionPlex
import actions.ActionPriority.Companion.BaseAction
import actions.ImActionPlex
import actions.actionables.IIdlor.Companion.Idle
import actions.actionables.IInstantiator.Companion.Destantiate
import actions.actionables.IInstantiator.Companion.Instantiate
import actions.actionables.IObservor.Companion.Look
import actions.actionables.IObservor.Companion.Reflect
import actions.actionables.IObservor.Companion.Watch
import com.soywiz.klock.DateTime
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.paint.LinearGradientPaint
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import state.ActionState
import state.StateAction
import templates.IInstance
import time.GlobalTimer
import time.GlobalTimer.mSecRenderStatusDelay
import time.Timer
import kotlin.time.ExperimentalTime

object RenderActionPlex {

    @ExperimentalUnsignedTypes
    val instances: RenderInstancePositionMap = mutableMapOf()

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

    fun Container.clear(xPos: Double, yPos: Double) {
        val checkTimer = Timer()

        this.solidRect(width = 150, height = 250, color = Colors["#2b2b2b"]).position(xPos, yPos)

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

    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance: IInstance) {

        val startingPosition = Point(50, 50)

        instances.toMap().forEach { pos ->
            val xPos = startingPosition.x + (pos.key % 6) * 150
            val yPos = startingPosition.y + (pos.key / 6) * 250

            if (pos.value == kInstance) container.clear(xPos - 25, yPos - 25)
        }

        instances.remove(instances.filterValues { it == kInstance }.keys.toList()[0])
    }

    var momentCounter = 0


    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    suspend fun renderInstance(instanceName : String, instanceLocation : Int, imActionPlex: ImActionPlex, startingPosition: Point) = coroutineScope {

        val checkTimer = Timer()

        val renderPosition = Point(x = startingPosition.x + (instanceLocation % 6) * 150, y = startingPosition.y + (instanceLocation / 6) * 250)

        container.clear(renderPosition.x - 25, renderPosition.y - 25)

        container.text(
            instanceName,
            font = font,
            textSize = 24.0,
            alignment = TextAlignment.BASELINE_LEFT
        ).position(renderPosition.x, renderPosition.y)

        var slotIdx = 0

        // val instancePlex = state(pos.value.actionPlex)

        //instancePlex.collect
        imActionPlex.forEach { slot ->

            renderSlot(slotIdx, slot.value, renderPosition)

            slotIdx++
        }
//        println("RenderActionPlex(renderInstance) @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()} $momentCounter")

    }

    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    fun renderSlot(slotIdx: Int, stateAction: StateAction, renderPos: Point) {

//        val checkTimer = Timer()

        val xSlotPos = renderPos.x
        val ySlotPos = renderPos.y + 25 + slotIdx * 25
        val fillText = stateAction.action.action
        val fillTextColor = when (stateAction.action) {
            Instantiate -> Colors["#37f585"]
            Destantiate -> Colors["#f58858"]
            Look -> Colors["#b9c3ff"]
            Watch -> Colors["#7978ff"]
            Reflect -> Colors["#4542ff"]
            Idle -> Colors["#f4ff1c"]
            else -> Colors["#f4ff1c"]
        }
        val fillColor = when (stateAction.actionPriority) {
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

        container.roundRect(80, 20, 1, 1, fillColor).position(xSlotPos, ySlotPos)
        container.text(fillText, textSize = 14.0, color = fillTextColor).position(xSlotPos, ySlotPos)

  //      println("RenderActionPlex(renderSlot) @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()} $momentCounter")

    }

    @ExperimentalUnsignedTypes
    val renderChannel = Channel<RenderData>()
/*
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun perform(timer: Timer): Timer = coroutineScope {

        val startingPosition = Point(50, 50)

//        val checkTimer = Timer()

        coroutineScope {
        while (!renderChannel.isEmpty) {
            val renderData : RenderData = renderChannel.receive()

            val renderInstanceEntry : Map.Entry<Int, IInstance>? = instances.filterValues { it.getInstanceId() == renderData.first }.entries.firstOrNull()

            if (renderInstanceEntry == null) {
                println("RenderActionPlex.perform() instance not found for uuid ${renderData.first}")
                return@coroutineScope Timer()
            }

//            println("render @ ${ DateTime.now() } ${renderInstanceEntry.value.getInstanceName()}")

            renderInstance(instanceName = renderInstanceEntry.value.getInstanceName(), instanceLocation = renderInstanceEntry.key, imActionPlex = renderData.second, startingPosition = startingPosition)
        }
}
//        println("RenderActionPlex @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()}, RT:${timer.getMillisecondsElapsed()}, $momentCounter")

        return@coroutineScope Timer()
    }
}
*/
@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun render(instanceId : UUID, imActionPlex: ImActionPlex) = coroutineScope {

        val checkTimer = Timer()
            val startingPosition = Point(50, 50)

            val renderInstanceEntry : Map.Entry<Int, IInstance>? = instances.filterValues { it.getInstanceId() == instanceId }.entries.firstOrNull()

            if (renderInstanceEntry == null) {
                println("RenderActionPlex.perform() instance not found for uuid $instanceId")
                return@coroutineScope Timer()
            }

            println("render @ ${ DateTime.now() } ${renderInstanceEntry.value.getInstanceName()} on $container")

            renderInstance(
                instanceName = renderInstanceEntry.value.getInstanceName(),
                instanceLocation = renderInstanceEntry.key,
                imActionPlex = imActionPlex,
                startingPosition = startingPosition
            )

        println("RenderActionPlex @ ${DateTime.now()} CT:${checkTimer.getMillisecondsElapsed()}")
        }

}


@ExperimentalUnsignedTypes
typealias RenderInstancePositionMap = MutableMap<Int, IInstance>

typealias RenderData = Pair<UUID, ImActionPlex>