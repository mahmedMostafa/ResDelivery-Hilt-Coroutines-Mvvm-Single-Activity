package com.example.resdelivery.features.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.resdelivery.R
import com.example.resdelivery.databinding.FragmentLoginBinding
import com.example.resdelivery.util.SessionManagement
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.getViewModel

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(), View.OnClickListener {


    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    private val sessionManagement: SessionManagement = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (sessionManagement.isLoggedIn()) {
            this.findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToFoodListFragment()
            )
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.loginButton.setOnClickListener(this)
        binding.signUpButton.setOnClickListener(this)
        subscribeToObservers()

        return binding.root
    }

    private fun subscribeToObservers() {
        viewModel = getViewModel()
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
                viewModel.doneShowingError()
            }
        })
        viewModel.navigateToMap.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    this.findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToFoodListFragment()
                    )
                    viewModel.doneNavigating()
                }
            }
        })
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun loginUser(email: String, password: String) {
        if (viewModel.validateEmail(email) == "ok") {

            if (viewModel.validatePassword(password) == "ok") {
                viewModel.loginUser(email, password)
            } else {
                binding.passwordEditText.error = viewModel.validatePassword(password)
            }
        } else {
            binding.emailEditText.error = viewModel.validateEmail(email)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_up_button -> {
                this.findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
            }
            R.id.login_button ->
                loginUser(
                    binding.emailEditText.editText?.text.toString().trim()
                    , binding.passwordEditText.editText?.text.toString().trim()
                )
        }
    }
}
