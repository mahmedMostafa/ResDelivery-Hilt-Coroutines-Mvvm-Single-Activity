package com.example.resdelivery.features.register


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.resdelivery.R
import com.example.resdelivery.databinding.FragmentRegisterBinding
import org.koin.android.viewmodel.ext.android.getViewModel

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment(), View.OnClickListener {


    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        binding.loginButton.setOnClickListener(this)
        binding.signUpButton.setOnClickListener(this)
        initViewModel()

        return binding.root
    }

    private fun initViewModel() {
        viewModel  = getViewModel()
        viewModel.navigateToMap.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    this.findNavController().navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToFoodListFragment()
                    )
                    viewModel.doneNavigating()
                }
            }
        })
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it)
                    binding.progressBar.visibility = View.VISIBLE
                else
                    binding.progressBar.visibility = View.INVISIBLE
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login_button ->
                this.findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )
            R.id.sign_up_button ->
                signUpUser(
                    binding.nameEditText.editText?.text.toString().trim(),
                    binding.emailEditText.editText?.text.toString().trim(),
                    binding.passwordEditText.editText?.text.toString().trim()
                )
        }
    }

    private fun signUpUser(name: String, email: String, password: String) {
        if (viewModel.validateUser(name, email, password) == "ok") {
            viewModel.signUp(email, password, name)
        }
    }
}
