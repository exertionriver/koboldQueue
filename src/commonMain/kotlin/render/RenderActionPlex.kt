package render

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
import com.soywiz.korge.ui.defaultUIFont
import templates.IInstance
import time.Moment
import time.Timer
import kotlin.time.ExperimentalTime

typealias Slot = MutableMap<Int, View>
@ExperimentalUnsignedTypes
typealias RenderInstancePositionMap = MutableMap<Int, IInstance>

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
object RenderActionPlex {

    @ExperimentalUnsignedTypes
    val instances: RenderInstancePositionMap = mutableMapOf()

    val renderHeaders : MutableMap<Int, View> = mutableMapOf()

    val renderSlotsBg : MutableMap<Int, Slot> = mutableMapOf()

    val renderSlotsFg : MutableMap<Int, Slot> = mutableMapOf()

    val renderSlotsText : MutableMap<Int, Slot> = mutableMapOf()

    val fancyPaint = LinearGradientPaint(0, 0, 0, 50).add(0.0, Colors.CADETBLUE).add(1.0, Colors.PURPLE)

    val fancyFont = BitmapFont(DefaultTtfFont, 64.0, paint = fancyPaint)


    fun lateInit(container: Container) {

        val startingPosition = Point(50, 50)

        val xOffset = 150
        val yOffset = 300

        //fill 12 text headers
        (0..11).toList().forEach { headerIdx -> renderHeaders.put(headerIdx, container.text(text = "init",
                font = fancyFont, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT
            ).position(startingPosition.x + (headerIdx % 6) * xOffset, startingPosition.y + (headerIdx / 6) * yOffset))
        }

        //fill 12 background slot rects
        (0..11).toList().forEach { queueIdx ->
            renderSlotsBg[queueIdx] =
                (0..7).toList().associate {slotIdx -> let {slotIdx to container.roundRect(80, 20, 1, 1, fill = Colors["#eaeaea"].withA(80))
                    .position(startingPosition.x + (queueIdx % 6) * xOffset, startingPosition.y + (queueIdx / 6) * yOffset + (slotIdx + 1) * 25)
                }
            }.toMutableMap()
        }

        //fill 12 foreground slot rects
        (0..11).toList().forEach { queueIdx ->
            renderSlotsFg[queueIdx] =
                (0..7).toList().associate {slotIdx -> let {slotIdx to container.roundRect(80, 20, 1, 1, fill = Colors["#eaeaea"])
                    .position(startingPosition.x + (queueIdx % 6) * xOffset, startingPosition.y + (queueIdx / 6) * yOffset + (slotIdx + 1) * 25)
                }
            }.toMutableMap()
        }

        //fill 12 slot texts
        (0..11).toList().forEach { queueIdx ->
            renderSlotsText[queueIdx] =
                (0..7).toList().associate {slotIdx -> let {slotIdx to container.text("init", textSize = 14.0, color = Colors["#3e3e3e"])
                    .position(startingPosition.x + (queueIdx % 6) * xOffset + 10, startingPosition.y + (queueIdx / 6) * yOffset + (slotIdx + 1) * 25)
                }
                }.toMutableMap()
        }

        (0..11).toList().forEach { queueIdx -> clearQueue(queueIdx) }
    }

    fun clearQueue(queueIdx : Int) {

        renderHeaders[queueIdx]!!.colorMul = Colors.BLACK
        renderHeaders[queueIdx]!!.setText("empty")
        (0..7).toList().forEach { slotIdx ->
            clearSlot(queueIdx, slotIdx)
        }
    }

    fun clearSlot(queueIdx : Int, slotIdx : Int) {

        renderSlotsBg[queueIdx]!![slotIdx]!!.colorMul = Colors.BLACK
        renderSlotsFg[queueIdx]!![slotIdx]!!.colorMul = Colors.BLACK
        renderSlotsText[queueIdx]!![slotIdx]!!.colorMul = Colors.BLACK
        renderSlotsText[queueIdx]!![slotIdx]!!.setText("empty")
    }

    fun renderQueue(queueIdx : Int, instanceName : String, instanceMoment : Moment, actionPlexMap: Map<UUID, StateAction>, interrupted: Boolean) {

        renderHeaders[queueIdx]!!.colorMul = Colors.CADETBLUE
        renderHeaders[queueIdx]!!.setText(instanceName)

        var slotIdx = 0

        actionPlexMap.forEach { slot ->
            (1..slot.value.plexSlotsFilled).toList().forEach {
                if (slotIdx > 7) return
                else renderSlot(queueIdx, slotIdx, instanceMoment, slot.value, interrupted)
                slotIdx++
            }
        }
    }

    fun renderSlot(queueIdx : Int, slotIdx : Int, instanceMoment : Moment, stateAction : StateAction, interrupted: Boolean) {

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
                    ActionPrepare -> Colors["#006c00"]
                    ActionExecute -> Colors["#080a6c"]
                    ActionRecover -> Colors["#6c0604"]
                    else -> Colors["#434241"]
                }
                else -> when (stateAction.actionState) {
                    ActionPrepare -> Colors["#00db00"]
                    ActionExecute -> Colors["#100be0"]
                    ActionRecover -> Colors["#e00508"]
                    else -> Colors["#727170"]
                }
            }
        }

        renderSlotsBg[queueIdx]!![slotIdx]!!.colorMul = fillColor.withA(180)
        renderSlotsFg[queueIdx]!![slotIdx]!!.colorMul = fillColor
        renderSlotsFg[queueIdx]!![slotIdx]!!.width = 80.0 * percentFilled
        renderSlotsText[queueIdx]!![slotIdx]!!.colorMul = fillTextColor
        renderSlotsText[queueIdx]!![slotIdx]!!.setText(fillText)
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


    fun addInstance(kInstance: IInstance) {

        instances.put(getOpenPosition(), kInstance)

    }

    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance: IInstance) {

        val instanceQueue = instances.filterValues { it == kInstance }.keys.toList()[0]

        clearQueue(instanceQueue)

        instances.remove(instanceQueue)
    }

    @KorgeInternal
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun render(instanceId : UUID, instanceMoment: Moment, actionPlexMap: Map<UUID, StateAction>, interrupted : Boolean = false) = coroutineScope {

        val checkTimer = Timer()

     //   val instanceViews: RenderInstanceViewMap = mutableMapOf()

        val renderInstanceEntry : Map.Entry<Int, IInstance>? = instances.filterValues { it.getInstanceId() == instanceId }.entries.firstOrNull()

        if (renderInstanceEntry == null) {
            println("RenderActionPlex.perform() instance not found for uuid $instanceId")
            return@coroutineScope //Timer()
        }

        renderQueue(
            queueIdx = renderInstanceEntry.key,
            instanceName = renderInstanceEntry.value.getInstanceName(),
            instanceMoment = instanceMoment,
            actionPlexMap = actionPlexMap,
            interrupted = interrupted
        )

        return@coroutineScope
    }
}
