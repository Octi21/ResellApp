package com.example.resellapp.shoppingCart

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController

import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentMyItemBinding
import com.example.resellapp.databinding.FragmentShoppingCartBinding
import com.example.resellapp.googlePay.util.PaymentsUtil
import com.example.resellapp.googlePay.viewmodel.CheckoutViewModel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.WalletConstants.PaymentMethod
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import com.stripe.android.PaymentConfiguration
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ShoppingCartFragment: Fragment() {

    private lateinit var binding: FragmentShoppingCartBinding
    private val model: CheckoutViewModel by viewModels()

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel

    private lateinit var googlePayButton: PayButton
    private lateinit var clientSecret: String

    private lateinit var PUBLISHABLE_KEY:String

    private val ApiURL:String = "https://us-central1-androidkotlinresellapp.cloudfunctions.net"

    var sum = 0.0
    var showButton = false

    var pkey = "pk_test_51Nh88KAl5tOl44uNAYlhBQBZNWxcprKNEUQPrpqYxxV0Qz3c0hbL6PwHbBXgA2M9fI3wb88GAPsK2F6FHxmpL9YV00FDjwr1jN"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_cart, container, false)

        val viewModelFactory = ShoppingCartViewModelFactory()

        shoppingCartViewModel = ViewModelProvider(this,viewModelFactory).get(ShoppingCartViewModel::class.java)

        binding.shoppingCartViewModel = shoppingCartViewModel






        val adapter = CartAdapter(shoppingCartViewModel)

        binding.itemsList.adapter = adapter

        shoppingCartViewModel.getItemsList().observe(viewLifecycleOwner, Observer {
            sum = 0.0
            Log.e("cartList", "${it}")
            Log.e("size","${it.size}")
            val size = it.size
            if(size ==1)
                binding.title.text = "Shopping Bag (1 item)"
            if(size == 0)
                binding.title.text = "Shopping Bag"
            else
                binding.title.text = "Shopping Bag ($size items)"
            for(item in it)
                sum += item.price!!

            Log.e("suma",sum.toString())
            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("#,##0.##")
            val formattedNumber = formatter.format(sum)

            if(size == 0)
            {
                binding.subtotalText.visibility = View.GONE
                binding.subtotal.visibility = View.GONE
                binding.transportText.visibility = View.GONE
                binding.transport.visibility = View.GONE
                binding.payButton.visibility = View.GONE
                showButton = false

            }
            else
            {
                binding.subtotalText.visibility = View.VISIBLE
                binding.subtotal.visibility = View.VISIBLE
                binding.transportText.visibility = View.VISIBLE
                binding.transport.visibility = View.VISIBLE
                binding.payButton.visibility = View.VISIBLE
                showButton = true


            }

            binding.subtotal.text = formattedNumber.toString() + "$"


            adapter.submitList(it)
        })


        binding.buyButton.setOnClickListener {
//            shoppingCartViewModel.buyItemFromCart()
        }

        shoppingCartViewModel.deletedList.observe(viewLifecycleOwner, Observer {
            Log.e("deletedItems",it.toString())
            shoppingCartViewModel.deleteIfInList(it)
        })

        shoppingCartViewModel.emptyListToast.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {
                Toast.makeText(requireContext(),"Shopping Bag Empty",Toast.LENGTH_SHORT).show()
                shoppingCartViewModel.resetToast()
            }
        })


        googlePayButton =    binding.payButton
        googlePayButton.initialize(
            ButtonOptions.newBuilder()
                .setButtonType(ButtonConstants.ButtonType.BUY)
                .setAllowedPaymentMethods(PaymentsUtil.allowedPaymentMethods.toString())
                .build()
        )

        PaymentConfiguration.init(requireContext(),pkey)


        val googlePayLauncher = GooglePayLauncher(
            fragment = this,
            config = GooglePayLauncher.Config(
                environment = GooglePayEnvironment.Test,
                merchantCountryCode = "RO",
                merchantName = "Widget Store" //"Google Pay showcase"   // "Widget Store"
            ),
            readyCallback = ::onGooglePayReady,
            resultCallback = ::onGooglePayResult
        )
//
        googlePayButton.setOnClickListener {
            // launch `GooglePayLauncher` to confirm a Payment Intent
            pay(googlePayLauncher)
        }



//        googlePayButton.setOnClickListener { requestPayment() }
//        model.canUseGooglePay.observe(viewLifecycleOwner, Observer(::setGooglePayAvailable))



        return binding.root
    }

    private fun initializePayment(){
        runBlocking{
            launch(Dispatchers.IO){
                val formattedNumber = DecimalFormat("0.00").format(sum)
                val ghe = formattedNumber.toDouble()
                Log.e("sum","${ghe}")
                val amount = (ghe * 100).toLong() + 500
                val (_,_,result) = ApiURL.plus("/stripePayment?amt=$amount")
                    .httpGet()
                    .responseJson()
                if(result is Result.Success){
                    val responseJson = result.get().obj()
                    clientSecret = responseJson.getString("paymentIntent")
                    val publicKey = responseJson.getString("publisableKey")
//                    PaymentConfiguration.init(requireContext(),publicKey)
                }
            }
        }
    }

    private fun pay(googlePayLauncher:GooglePayLauncher){
        try{
            initializePayment()

            googlePayLauncher.presentForPaymentIntent(clientSecret)
        }catch(e:Exception){
            Toast.makeText(
                requireContext(),
                "Google Pay is not available on this device",
                Toast.LENGTH_SHORT
            ).show()
        }

    }




    private fun onGooglePayReady(isReady: Boolean) {
        if(showButton)
            googlePayButton.visibility = View.VISIBLE
        googlePayButton.isEnabled = isReady
    }

    private fun onGooglePayResult(result: GooglePayLauncher.Result) {
        when (result) {
            GooglePayLauncher.Result.Completed -> {
                // Payment succeeded, show a receipt view
                shoppingCartViewModel.buyItemFromCart()

                val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToSuccessFragment()
                findNavController().navigate(action)
            }
            GooglePayLauncher.Result.Canceled -> {
                handleError(CommonStatusCodes.CANCELED, "Google Pay canceled")

            }
            is GooglePayLauncher.Result.Failed -> {
                handleError(CommonStatusCodes.ERROR, result.error.message)


            }
        }
    }
    private fun handleError(statusCode: Int, message: String?) {
        Log.e(TAG, "Error code: $statusCode, Message: $message")
    }






    //google pay button without stripe

//    private fun setGooglePayAvailable(available: Boolean) {
//        if (available) {
//            googlePayButton.visibility = View.VISIBLE
//        } else {
//            Toast.makeText(
//                requireContext(),
//                "Unavailable",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    private fun requestPayment() {
//
//        // Disables the button to prevent multiple clicks.
//        googlePayButton.isClickable = false
//
//        // The price provided to the API should include taxes and shipping.
//        // This price is not displayed to the user.
//        val dummyPriceCents = 200L
//        val shippingCostCents = 900L
//        val task = model.getLoadPaymentDataTask(dummyPriceCents + shippingCostCents)
//
//        task.addOnCompleteListener { completedTask ->
//            if (completedTask.isSuccessful) {
//                completedTask.result.let(::handlePaymentSuccess)
//            } else {
//                when (val exception = completedTask.exception) {
//                    is ResolvableApiException -> {
//                        resolvePaymentForResult.launch(
//                            IntentSenderRequest.Builder(exception.resolution).build()
//                        )
//                    }
//
//                    is ApiException -> {
//                        handleError(exception.statusCode, exception.message)
//                    }
//
//                    else -> {
//                        handleError(
//                            CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
//                                    " exception when trying to deliver the task result to an activity!"
//                        )
//                    }
//                }
//            }
//
//            // Re-enables the Google Pay payment button.
//            googlePayButton.isClickable = true
//        }
//    }
//
//    // Handle potential conflict from calling loadPaymentData
//    private val resolvePaymentForResult =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
//            when (result.resultCode) {
//                RESULT_OK ->
//                    result.data?.let { intent ->
//                        PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
//                    }
//
//                RESULT_CANCELED -> {
//                    // The user cancelled the payment attempt
//                }
//            }
//        }
//
//    /**
//     * PaymentData response object contains the payment information, as well as any additional
//     * requested information, such as billing and shipping address.
//     *
//     * @param paymentData A response object returned by Google after a payer approves payment.
//     * @see [Payment
//     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
//     */
//    private fun handlePaymentSuccess(paymentData: PaymentData) {
//        val paymentInformation = paymentData.toJson()
//
//        try {
//            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
//            val paymentMethodData =
//                JSONObject(paymentInformation).getJSONObject("paymentMethodData")
//            val billingName = paymentMethodData.getJSONObject("info")
//                .getJSONObject("billingAddress").getString("name")
//            Log.d("BillingName", billingName)
//
//            // Logging token string.
//            Log.d(
//                "Google Pay token", paymentMethodData
//                    .getJSONObject("tokenizationData")
//                    .getString("token")
//            )
//
////            startActivity(Intent(requireContext(), CheckoutSuccessActivity::class.java))
//
//        } catch (error: JSONException) {
//            Log.e("handlePaymentSuccess", "Error: $error")
//        }
//    }
//
//    /**
//     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
//     * only logging is required.
//     *
//     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
//     * WalletConstants.ERROR_CODE_* constants.
//     * @see [
//     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
//     */
//    private fun handleError(statusCode: Int, message: String?) {
//        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
//    }


}