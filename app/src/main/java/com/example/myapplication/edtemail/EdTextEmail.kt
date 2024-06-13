package com.example.myapplication.edtemail

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.myapplication.R

class EdTextEmail @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = s.toString()
                when {
                    email.isBlank() -> error = context.getString(R.string.email_empty)
                    !email.isEmailValid() -> error = context.getString(R.string.email_wrong)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}