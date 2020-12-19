package com.example.chameleody.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.chameleody.R

class MoodFragment : Fragment() {
    private val moods: Array<Array<String>> = arrayOf(
        arrayOf("peac\neful", "easy\ngoing", "upbeat\n", "lively\n", "exci\nted"),
        arrayOf("tender\n", "roman\ntic", "empo\nwering", "stir\nring", "rowdy\n"),
        arrayOf("senti\nmental", "sophis\nticated", "sensual\n", "fiery\n", "energi\nzing"),
        arrayOf("melan\ncholy", "cool\n", "year\nning", "urgent\n", "defiant\n"),
        arrayOf("somber\n", "gritty\n", "ser\nious", "broo\nding", "aggre\nssive"),
        arrayOf("not set\n")
    )
    private val colors: Array<Array<String>> = arrayOf(
        arrayOf("#84ff84", "#c6ff42", "#ffff00", "#ffc600", "#ff8400", ),
        arrayOf("#42ffc6", "#a1ffa1", "#ffff84", "#ffa142", "#ff4200", ),
        arrayOf("#00ffff", "#84ffff", "#ffffff", "#ff8484", "#ff0000", ),
        arrayOf("#42c6ff", "#a1a1ff", "#ff84ff", "#ff42a1", "#ff0042", ),
        arrayOf("#8484ff", "#c642ff", "#ff00ff", "#ff00c6", "#ff0084", ),
        arrayOf("#ffffff")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val layout: LinearLayout = inflater.inflate(R.layout.fragment_mood, container, false) as LinearLayout
        createTable(layout)
        return layout
    }

    private lateinit var texts: Array<Array<TextView>>
    private lateinit var noTxt: TextView
    private fun createTable(layout: LinearLayout){
        val table : TableLayout = layout.getChildAt(1) as TableLayout
        texts = Array(5) { row -> Array(5) { col -> createEntry(row, col) } }
        noTxt = createEntry(5,0)
        for (rowTexts in texts){
            val row = TableRow(activity)
            for (colText in rowTexts){
                row.addView(colText)
            }
            table.addView(row)
        }
        val row = TableRow(activity)
        row.addView(Space(activity))
        row.addView(Space(activity))
        row.addView(noTxt)
        row.addView(Space(activity))
        row.addView(Space(activity))
        table.addView(row)
    }

    var slc: Int = 0
    private fun createEntry(row: Int, col:Int): TextView{
        val tv = TextView(activity)
        tv.text = moods[row][col]
        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            1.0f)
        tv.gravity = Gravity.CENTER
        val dp: Int = resources.displayMetrics.density.toInt()
        params.setMargins(5*dp, 5*dp, 5*dp, 5*dp)
        tv.layoutParams = params
        tv.setBackgroundResource(R.drawable.rounded_corner)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tv.backgroundTintList = ColorStateList.valueOf(setDefaultColor(row, col))
        }
        tv.setOnClickListener {
            val r = if(slc==0) 5 else (slc-1)/5
            val c = if(slc==0) 0 else (slc-1)%5
            val selectedTV = if(slc==0) noTxt else texts[r][c]
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                selectedTV.backgroundTintList = ColorStateList.valueOf(setDefaultColor(r, c))
                tv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colors[row][col]))
            }
            selectedTV.setTypeface(null, Typeface.NORMAL)
            tv.setTypeface(null, Typeface.BOLD)
            slc = if (row==5) 0 else row*5+col+1
        }
        return tv
    }

    private fun setDefaultColor(row: Int, col: Int) : Int{
        val str = colors[row][col]
        var color = Color.parseColor(str)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.5f
        color = Color.HSVToColor(hsv)
        return color
    }
}