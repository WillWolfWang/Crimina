package com.will.criminalintent.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.will.criminalintent.R
import com.will.criminalintent.utils.getScaledBitmap

private const val ARG_DATE = "filePath"
class PhotoDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val filePath = arguments?.getString(ARG_DATE)
        Log.e("WillWolf", "filePath-->" + filePath)

        val dialog : Dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_fragment_photo)
        val ivPhoto = dialog.findViewById<ImageView>(R.id.iv_photo)
        ivPhoto.setImageBitmap(getScaledBitmap(filePath!!, requireActivity()))

        ivPhoto.setOnClickListener{
            dismiss()
        }

        return dialog
    }

    companion object {
        fun newInstance(filePath: String): PhotoDialogFragment {

            val bundle: Bundle = Bundle().apply {
                putString(ARG_DATE, filePath)
            }

            return PhotoDialogFragment().apply {
                arguments = bundle
            }
        }
    }
}