package sayan.apps.numplex

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FeedbackFragment : Fragment(), View.OnClickListener {

    private lateinit var feedbackEditText: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var submitButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.feedbackPage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        feedbackEditText = view.findViewById(R.id.fb_feedback)
        ratingBar = view.findViewById(R.id.fb_rating)
        submitButton = view.findViewById(R.id.btn_submit)
        submitButton.setOnClickListener(this)
        ratingBar.stepSize = 1f
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                val feedbackText = feedbackEditText.text.toString()
                val ratingValue = ratingBar.rating.toDouble()

                val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                val userId = account?.email ?: "defaultUserId"

                val ratingMap = HashMap<String, Any>()
                ratingMap["userId"] = userId
                ratingMap["ratingValue"] = ratingValue
                ratingMap["feedbackText"] = feedbackText

                database.child("ratings").push().setValue(ratingMap)
                Toast.makeText(requireActivity(), "Thank you for the feedback!", Toast.LENGTH_SHORT).show()
                feedbackEditText.setText("")
                ratingBar.rating = 0f
                hideKeyboard()
            }
        }
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(requireContext())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
