package render

import actions.ActionPlex
import actions.ActionPriority.Companion.BaseAction
import actions.IActionPlex
import actions.actionables.IIdlor.Companion.Idle
import actions.actionables.IInstantiator.Companion.Destantiate
import actions.actionables.IInstantiator.Companion.Instantiate
import actions.actionables.IObservor.Companion.Look
import actions.actionables.IObservor.Companion.Reflect
import actions.actionables.IObservor.Companion.Watch
import com.soywiz.klock.DateTime
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.effect.BitmapEffect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.paint.LinearGradientPaint
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import state.ActionState
import state.StateAction
import templates.IInstance
import time.GlobalTimer.mSecPerceptionDelay
import time.GlobalTimer.mSecRenderDelay
import time.Timer
import kotlin.time.ExperimentalTime

object RenderActionPlex {

    @ExperimentalUnsignedTypes
    val instances : RenderInstancePositionMap = mutableMapOf()

    lateinit var container : Container //set this with Korge.rootContainer before running anything

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

    fun Container.clear(xPos : Double, yPos : Double) {
        this.solidRect(width = 150, height = 250, color = Colors["#2b2b2b"]).position(xPos, yPos)
    }

    @ExperimentalUnsignedTypes
    fun getOpenPosition() : Int {
        var curPosIdx = 0
        val sizeIdx = instances.size - 1

        while (curPosIdx <= sizeIdx) {

            if (instances[curPosIdx] == null) return curPosIdx

            curPosIdx++
        }

        return instances.size
    }

    @ExperimentalUnsignedTypes
    fun state(actionPlex : ActionPlex) : Flow<StateAction> = flow {

        actionPlex.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() }).forEach{ emit(it.second) }

    }

    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance : IInstance) {

        val startingPosition = Point(50, 50)

        instances.toMap().forEach { pos ->
            val xPos = startingPosition.x + (pos.key % 6) * 150
            val yPos = startingPosition.y + (pos.key / 6) * 250

            if (pos.value == kInstance) container.clear(xPos - 25, yPos - 25)
        }

        instances.remove(instances.filterValues{ it == kInstance }.keys.toList()[0] )
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun perform(timer : Timer) : Timer = coroutineScope {

        val startingPosition = Point(50, 50)

        instances.toMap().forEach { pos ->

            val xPos = startingPosition.x + (pos.key % 6) * 150
            val yPos = startingPosition.y + (pos.key / 6) * 250

            container.clear(xPos - 25, yPos - 25)

            container.text(
                pos.value.getInstanceName(),
                font = font,
                textSize = 24.0,
                alignment = TextAlignment.BASELINE_LEFT
            ).position(xPos, yPos)

            var slotIdx = 0

            val instancePlex = state(pos.value.actionPlex)

            //instancePlex.collect
            pos.value.actionPlex.toMap().forEach { slot ->
                val xSlotPos = xPos
                val ySlotPos = yPos + 25 + slotIdx * 25
                val fillText = slot.value.action.action
                val fillTextColor = when (slot.value.action) {
                    Instantiate -> Colors["#37f585"]
                    Destantiate -> Colors["#f58858"]
                    Look -> Colors["#b9c3ff"]
                    Watch -> Colors["#7978ff"]
                    Reflect -> Colors["#4542ff"]
                    Idle -> Colors["#f4ff1c"]
                    else -> Colors["#f4ff1c"]
                }
                val fillColor = when (slot.value.actionPriority) {
                    BaseAction -> when (slot.value.actionState) {
                        ActionState.ActionPrepare -> Colors["#006c00"]
                        ActionState.ActionExecute -> Colors["#080a6c"]
                        ActionState.ActionRecover -> Colors["#6c0604"]
                        else -> Colors["#434241"]
                    }
                    else -> when (slot.value.actionState) {
                        ActionState.ActionPrepare -> Colors["#00db00"]
                        ActionState.ActionExecute -> Colors["#100be0"]
                        ActionState.ActionRecover -> Colors["#e00508"]
                        else -> Colors["#727170"]
                    }
                }

                container.roundRect(80, 20, 1, 1, fillColor).position(xSlotPos, ySlotPos)
                container.text(fillText, textSize = 14.0, color = fillTextColor).position(xSlotPos, ySlotPos)

                slotIdx++
            }
        }
        return@coroutineScope Timer()
    }
}

@ExperimentalUnsignedTypes
typealias RenderInstancePositionMap = MutableMap<Int, IInstance>
