package render

import actions.ActionPlex
import actions.state
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.effect.BitmapEffect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.paint.LinearGradientPaint
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import templates.IInstance


object RenderActionPlex {

    @ExperimentalUnsignedTypes
    val instances : RenderInstancePositionMap = mutableMapOf()

//    @ExperimentalUnsignedTypes
//    fun remove(instanceName : String) {
//        instances.filterKeys { it.getInstanceName() == instanceName }.forEach {instances.remove(it as IInstance)}

//        println("removing instanceName from RenderInstandPositionMap!")
//    }

    lateinit var container : Container //set this with Korge.rootContainer before running anything

    val font = BitmapFont(
        DefaultTtfFont, 64.0,
        paint = LinearGradientPaint(0, 0, 0, 50).add(0.0, Colors.CADETBLUE).add(1.0, Colors.PURPLE),
        effect = BitmapEffect(
            dropShadowX = 2,
            dropShadowY = 2,
            dropShadowRadius = 2,
            dropShadowColor = Colors["#5f005f"]
        )
    )

    fun Container.clear() {
        this.solidRect(width = 1024, height = 1024, color = Colors["#2b2b2b"]).position(0, 0)
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
    suspend fun ActionPlex.renderActionPlex() {

        val position = Point(50, 50)

        container.clear()

        instances.forEach {
            val xPos = position.x + (it.key % 6) * 150
            val yPos = position.y + (it.key / 6) * 250
            container.text(it.value.getInstanceName(), font = font, textSize = 20.0, alignment = TextAlignment.BASELINE_LEFT).position(xPos, yPos)
        }
   }

}

@ExperimentalUnsignedTypes
typealias RenderInstancePositionMap = MutableMap<Int, IInstance>

@ExperimentalUnsignedTypes
typealias RenderStateActionSlotMap = MutableMap<UUID, Int>
