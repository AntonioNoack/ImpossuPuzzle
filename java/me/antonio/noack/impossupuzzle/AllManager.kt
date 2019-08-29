package me.antonio.noack.impossupuzzle

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.all.*
import kotlinx.android.synthetic.main.ask.*
import kotlinx.android.synthetic.main.info.*
import kotlinx.android.synthetic.main.menu.*
import kotlinx.android.synthetic.main.menu.difficulty
import kotlinx.android.synthetic.main.menu.title
import kotlinx.android.synthetic.main.name.*
import kotlinx.android.synthetic.main.saveentry.view.*
import kotlinx.android.synthetic.main.savelist.*
import me.antonio.noack.gl.Initializable
import me.antonio.noack.impossupuzzle.Saver.save
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.math.BigInteger
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.sqrt

class AllManager : AppCompatActivity() {

    lateinit var gameView: GameView

    lateinit var pref: SharedPreferences

    companion object {
        const val MAX_LEVEL = 25
        lateinit var showWon: () -> Unit
    }

    lateinit var localSaves: File

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.all)
        actionBar?.hide()

        pref = getPreferences(Context.MODE_PRIVATE)

        if(Build.VERSION.SDK_INT >= 21){
            window.navigationBarColor = 0xff000000.toInt()
        }

        localSaves = filesDir
        localSaves.mkdirs()

        gameView = GameView()
        glView.renderTask = gameView

        openMenu(false, null)

        menuButton.setOnClickListener {
            openMenu(true, null)
        }

        saveButton.setOnClickListener {
            // show save menu: enter the name or chose an existing one and overrite it
            showSaveMenu()
        }

        resetButton.setOnClickListener {
            askIfSure(R.string.ask_reset){
                gameView.game?.resetGame()
            }
        }

        infoButton.setOnClickListener {
            showInfo()
        }

        showWon = {
            openMenu(true, getString(R.string.won_msg))
        }

    }

    fun showInfo(){

        val info = AlertDialog.Builder(this)
            .setView(R.layout.info)
            .show()

        val game = gameView.game ?: gameView.getExample()

        info.countWidth.text = game.sx.toString()
        info.countHeight.text = game.sy.toString()
        info.countDifficulty.text = game.df.toString()
        info.countStatesPerEntity.text = game.states.toString()
        info.countSwaps.text = game.swaps.toString()
        info.countStates.text = "%d".format(BigInteger.valueOf(game.states.toLong()).pow(game.sx * game.sy).minus(BigInteger.ONE))

    }

    fun showSaveMenu(){

        val menu = AlertDialog.Builder(this)
            .setView(R.layout.savelist)
            .show()

        val list = menu.list

        val entry0 = layoutInflater.inflate(R.layout.saveentry, list, false)
        entry0.text.text = getString(R.string.new_save)
        list.addView(entry0)

        entry0.setOnClickListener {
            // enter name
            // save it...
            val menu2 = AlertDialog.Builder(this)
                .setView(R.layout.name)
                .show()
            menu2.okButton.setOnClickListener {
                val name = menu2.name.text.trim()
                if(name.isBlank()){
                    Toast.makeText(this, R.string.have_to_enter_name, Toast.LENGTH_SHORT).show()
                } else {
                    val file = File(localSaves, "${System.currentTimeMillis()}.${localSaves.list().size}.game")
                    file.parentFile.mkdirs()
                    save(gameView.game!!, name.toString(), DataOutputStream(file.outputStream().buffered()))
                    menu2.dismiss()
                    menu.dismiss()
                    Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
                }
            }
        }

        for(file in localSaves.listFiles()){
            if(file.name.endsWith(".game")){
                val entry = layoutInflater.inflate(R.layout.saveentry, list, false)
                val input = DataInputStream(file.inputStream())
                val name = input.readUTF()
                entry.text.text = name
                input.close()
                list.addView(entry)
                entry.setOnClickListener {
                    save(gameView.game!!, name, DataOutputStream(file.outputStream().buffered()))
                    menu.dismiss()
                }
                entry.setOnLongClickListener {
                    askIfSure(R.string.ask_delete){
                        entry.visibility = View.GONE
                        thread { file.delete() }
                    }
                    true
                }
            }
        }

    }

    fun showLoadList(dialog: AlertDialog){

        start@ do {
            for(file in localSaves.listFiles()){
                if(file.name.endsWith(".game")){
                    break@start
                }
            }
            Toast.makeText(this, R.string.no_saves, Toast.LENGTH_SHORT).show()
            return
        } while (false)

        val menu = AlertDialog.Builder(this)
            .setView(R.layout.savelist)
            .show()

        val list = menu.list
        for(file in localSaves.listFiles()){
            if(file.name.endsWith(".game")){
                val entry = layoutInflater.inflate(R.layout.saveentry, list, false)
                val input = DataInputStream(file.inputStream())
                entry.text.text = input.readUTF()
                input.close()
                list.addView(entry)
                entry.setOnClickListener {
                    load(DataInputStream(file.inputStream().buffered()))
                    menu.dismiss()
                    dialog.dismiss()
                }
                entry.setOnLongClickListener {
                    askIfSure(R.string.ask_delete){
                        entry.visibility = View.GONE
                        thread { file.delete() }
                    }
                    true
                }
            }
        }

    }

    fun load(dis: DataInputStream){
        val name = dis.readUTF()
        val con = StorageConnector(dis)
        val game = Game(con.sx, con.sy, con.df, con.initPerDot, con.states, con)
        startGame(game)
    }

    fun askIfSure(@StringRes msg: Int, onSuccess: () -> Unit){

        val ask = AlertDialog.Builder(this)
            .setView(R.layout.ask)
            .setCancelable(true)
            .show()

        ask.question.setText(msg)

        ask.yes.setOnClickListener {
            onSuccess()
            ask.dismiss()
        }

        ask.no.setOnClickListener {
            ask.dismiss()
        }

    }

    fun openMenu(cancellable: Boolean, title: String?){

        val menu = AlertDialog.Builder(this)
            .setView(R.layout.menu)
            .setCancelable(cancellable)
            .show()

        if(title != null){
            menu.title.visibility = View.VISIBLE
            menu.titleText.text = title
        }

        val sx = menu.sx
        sx.value = pref.getInt("sx", 2)
        sx.minValue = 2
        sx.maxValue = 25

        val sy = menu.sy
        sy.value = pref.getInt("sy", 2)
        sy.minValue = 2
        sy.maxValue = 25

        val df = menu.difficulty
        df.value = pref.getInt("df", 1)
        df.minValue = 1
        df.maxValue = MAX_LEVEL

        menu.allowAsymmetric.isChecked = pref.getBoolean("asym", true)
        menu.allowAsymmetric.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("asym", isChecked).apply()
        }

        menu.start.setOnClickListener {
            if(sx.value == 1 && sy.value == 1){
                Toast.makeText(this, R.string.please_sx_sy_one, Toast.LENGTH_SHORT).show()
            } else {
                pref.edit()
                    .putInt("sx", sx.value)
                    .putInt("sy", sy.value)
                    .putInt("df", df.value)
                    .apply()
                startGame(sx.value, sy.value, df.value, menu.allowAsymmetric.isChecked)
                menu.dismiss()
            }
        }

        menu.loadButton.setOnClickListener {
            showLoadList(menu)
        }

    }

    fun startGame(sx: Int, sy: Int, df: Int, asym: Boolean){
        val game = Game(sx, sy, df, df * 0.2f, 2 + sqrt(df * Math.random()).toInt(), object: Connector {
            override fun connect(game: Game) {
                val conPerDot = 1 + sx * sy * 0.25f * df / MAX_LEVEL
                val cpdFloor = floor(conPerDot).toInt()
                val cpdFract = conPerDot - floor(conPerDot)
                val fields = game.fields
                for(x in 0 until sx){
                    for(y in 0 until sy){
                        val count = cpdFloor + (if(Math.random() < cpdFract) 1 else 0)
                        val field = game.get(x, y)
                        for(k in 0 until count){
                            if(asym && Math.random() < 0.1){
                                // asymmetric
                                field.connect(fields.random())
                            } else {
                                val second = fields.random()
                                field.connect(second)
                                second.connect(field)
                            }
                        }
                    }
                }
                game.initTask()
            }
        })
        startGame(game)
    }

    fun startGame(game: Game){
        gameView.game = game
        Toast.makeText(this, R.string.make_all_black, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        gameView.exampleGame?.fields?.forEach { it.clear() }
        gameView.exampleGame = null
        Initializable.reset()
    }

}
