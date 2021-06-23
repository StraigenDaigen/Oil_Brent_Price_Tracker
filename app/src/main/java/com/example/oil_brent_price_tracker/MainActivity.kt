package com.example.oil_brent_price_tracker

//import com.example.oil_brent_price_tracker.ml.ModelTesla
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.oil_brent_price_tracker.newsDataCollection.newsDataItem
import com.example.oil_brent_price_tracker.pricesDataCollection.pricesDataItem
import com.example.oil_brent_price_tracker.tabs.PagerAdaptar
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager
    private lateinit var predictBtn: ImageButton
    private lateinit var newsBtn: ImageButton
    private lateinit var infoBtn: ImageButton
    private lateinit var mPagerAdapter: PagerAdapter
    private lateinit var tflite: Interpreter
    private lateinit var tfliteModel: ByteBuffer
    private val TAG = "MainActivity::TFLite"



    private fun loadModel(modelPath: String): ByteBuffer {
        val fileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val retFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        fileDescriptor.close()
        return retFile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tfliteModel = loadModel("model_tesla.tflite")
        tflite = Interpreter(tfliteModel)
        Log.d(TAG, tflite.outputTensorCount.toString())
        // init view
        var tensor = tflite.getInputTensor(0)
        Log.d(TAG,tensor.name())
        Log.d(TAG,tensor.shapeSignature().map { it.toString() }.joinToString(","))
        Log.d(TAG,tensor.dataType().name)

        tensor = tflite.getOutputTensor(0)
        Log.d(TAG,tensor.name())
        Log.d(TAG,tensor.shapeSignature().map { it.toString() }.joinToString(","))
        Log.d(TAG,tensor.dataType().name)

        mViewPager = findViewById(R.id.mViewPager)

        // init image buttons
        predictBtn =findViewById(R.id.predictBtn)
        newsBtn = findViewById(R.id.newsBtn)
        infoBtn = findViewById(R.id.infoBtn)

        mPagerAdapter = PagerAdaptar(supportFragmentManager)
        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 5

        // add page change listener
        var counter: Int

        counter = 0

        mViewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }




            override fun onPageSelected(position: Int) {
                    changingTabs(position)

            }
        })
        // default tab
        mViewPager.currentItem = 1
        //infoBtn.setImageResource(R.drawable.ic_baseline_info_26_orange)
    }

    private fun changingTabs(position: Int) {



        val boton_sentiment_analyse = findViewById<Button>(R.id.btnclassify2)
        val boton_predict = findViewById<Button>(R.id.button_predict_price)

        println(position.toString())

        if(position == 0){

            predictBtn.setImageResource(R.drawable.ic_baseline_equalizer_24_orange)
            newsBtn.setImageResource(R.drawable.ic_baseline_import_export_25)
            infoBtn.setImageResource(R.drawable.ic_baseline_info_26)

            boton_predict.setOnClickListener{v -> callServiceGetPrices()}



        }

        else if(position == 2){


            predictBtn.setImageResource(R.drawable.ic_baseline_equalizer_24)
            newsBtn.setImageResource(R.drawable.ic_baseline_import_export_25_orange)
            //infoBtn.setImageResource(R.drawable.ic_baseline_info_26_orange)
            infoBtn.setImageResource(R.drawable.ic_baseline_info_26)

            boton_sentiment_analyse?.setOnClickListener { callServiceGetSentimentAnalyze() }





        }

        else if(position == 1){

            predictBtn.setImageResource(R.drawable.ic_baseline_equalizer_24)

            newsBtn.setImageResource(R.drawable.ic_baseline_import_export_25)
            infoBtn.setImageResource(R.drawable.ic_baseline_info_26_orange)
        }
    }

    private fun callServiceGetPrices(){

        val userService: UserService = RestEngine.getRestEngine().create(UserService::class.java)
        val result: Call<List<pricesDataItem>> = userService.listPrices()

        var array_size: Int
        var prices_array: MutableList<Double> = mutableListOf()
        var prices_array_originals: MutableList<Double> = mutableListOf()
        var prices_array_size: String
        var std: Double
        var mean: Double
        var desnormalize: Double
        var normalize: Double
        var originals_data: Double



        result.enqueue(object : Callback<List<pricesDataItem>>{
            override fun onFailure(call: Call<List<pricesDataItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<pricesDataItem>>,
                response: Response<List<pricesDataItem>>
            )
            {
                //Toast.makeText(this@MainActivity, "OK", Toast.LENGTH_LONG).show()

                std = 119.111
                mean = 186.413

                array_size = response.body()?.count()!!
                prices_array.clear()

                for (i in array_size-9..array_size){

                    normalize = ((response.body()!![i-1].close)-mean)/std
                    originals_data = response.body()!![i-1].close
                    //normalize = (response.body()!![i-1].close)
                    prices_array += normalize
                    prices_array_originals += originals_data


                }


                //prices_array += response.body()!![20].close
                //prices_array += response.body()!![21].close
                //prices_array= arrayOf(800.0, 500.0, 550.0, 600.0, 650.0, 780.0, 600.0, 605.0, 589.0, 708.0, 499.0).toMutableList()
                prices_array_size = prices_array.count().toString()

                val lista_precios_text = findViewById<TextView>(R.id.price_view)

                lista_precios_text.text = prices_array_originals.toString()

                Toast.makeText(this@MainActivity, "Ultimos 10 Precios Diarios Cargados", Toast.LENGTH_LONG).show()

                val inputs = arrayOf(prices_array.map { it.toFloat() }.toFloatArray())
                val outputs = arrayOf(FloatArray(1))
                tflite.run(inputs, outputs)
                val normalizedValue = outputs.first()[0]
                val unormalizedValue = normalizedValue * std + mean
                var value = "%.2f".format(unormalizedValue).toDouble()
                Log.d(TAG, value.toString())
                val prediction_price_text = findViewById<TextView>(R.id.prediccion_price_view)
                prediction_price_text.text = value.toString()

//                outputs.forEach {
//                    it.forEachIndexed { index, fl ->
//                        Log.d(TAG, fl.toString())
//                    }
//                }


            }



        })




    }







    private fun callServiceGetSentimentAnalyze(){


        val userServiceAnalyze: UserServiceNews = RestEngine.getRestEngineAnalyse().create(UserServiceNews::class.java)


        //SE CAPTURA EL TEXTO QUE ESTA EN EL CUADRO DE NOTICIAS Y SE ENVIA POSTERIORMENTE A LA PETICION GET
        val txtSentence = findViewById<TextView>(R.id.txtSentences)
        val text = txtSentence.text.toString()

        println(userServiceAnalyze.listSentiments(text))
        val result: Call<List<newsDataItem>> = userServiceAnalyze.listSentiments(text)
        var sentiment: String
        var score: Double


        result.enqueue(object : Callback<List<newsDataItem>>{
            override fun onFailure(call: Call<List<newsDataItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<newsDataItem>>,
                response: Response<List<newsDataItem>>
            )
            {


                sentiment = response.body()!![0].label
                score = (response.body()!![0].score)*100

                //val number2digits:Double = (score * 100.0).roundToInt() / 100.0

                score = "%.2f".format(score).toDouble()


                Toast.makeText(this@MainActivity, sentiment, Toast.LENGTH_LONG).show()
                Toast.makeText(this@MainActivity, score.toString(), Toast.LENGTH_LONG).show()


                val sentiment_analysis = findViewById<TextView>(R.id.analyze_view)
                sentiment_analysis.text = sentiment

                val percentage_inference = findViewById<TextView>(R.id.percentage_number_view)
                percentage_inference.text = score.toString() + "%"

                val text = findViewById<View>(R.id.percentage_number_view) as TextView
                text.setTextColor(Color.parseColor("#000000"))

                if (sentiment == "POSITIVE"){

                    val text = findViewById<View>(R.id.analyze_view) as TextView
                    text.setTextColor(Color.parseColor("#2D572C"))
                }

                else if (sentiment == "NEGATIVE"){

                    val text = findViewById<View>(R.id.analyze_view) as TextView
                    text.setTextColor(Color.parseColor("#FF0000"))
                }

//                outputs.forEach {
//                    it.forEachIndexed { index, fl ->
//                        Log.d(TAG, fl.toString())
//                    }
//                }


            }



        })




    }


}


private fun ByteBuffer.put(pricesArray: MutableList<Double>) {

}

private fun TensorBuffer.loadBuffer(byteBuffer: MutableList<Double>) {

}
