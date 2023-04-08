package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kedaimbaktimapp.databinding.FragmentHistoryBinding
import com.example.kedaimbaktimapp.databinding.FragmentLogoutBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoutFragment : Fragment() {

    private lateinit var binding: FragmentLogoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLogoutBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        getActivity()?.let {
//            AlertDialog.Builder(it)
//                .setTitle(getString(R.string.logout))
//                .setMessage(getString(R.string.want_to_logout))
//                .setPositiveButton(getString(R.string.yes)) { _, _ ->
//                    signOut()
//                    Toast.makeText(
//                        getActivity()?.getApplicationContext(),
//                        getString(R.string.success_logout),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                .setNegativeButton(getString(R.string.no)) { _, _ -> }
//                .show()
//        }
    }

     override fun onStart() {
        super.onStart()
        signOut()
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
        Toast.makeText(getActivity(),"Logout berhasil",Toast.LENGTH_SHORT).show();

    }

}