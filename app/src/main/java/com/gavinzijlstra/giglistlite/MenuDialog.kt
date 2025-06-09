package com.gavinzijlstra.giglistlite

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment

class MenuDialog(
    private val currentField: String,
    private val onFieldSelected: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_menu, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        // Selecteer de juiste knop vooraf
        when (currentField.lowercase()) {
            "band" -> radioGroup.check(R.id.radioBand)
            "venue" -> radioGroup.check(R.id.radioVenue)
            "datum" -> radioGroup.check(R.id.radioDatum)
            "opmerking" -> radioGroup.check(R.id.radioOpmerking)
        }

        builder.setView(view)
            .setTitle("Zoek op:")
            .setPositiveButton("OK") { dialog, _ ->
                val selectedId = radioGroup.checkedRadioButtonId
                val selectedText = when (selectedId) {
                    R.id.radioBand -> "band"
                    R.id.radioVenue -> "venue"
                    R.id.radioDatum -> "datum"
                    R.id.radioOpmerking -> "opmerking"
                    else -> currentField
                }
                onFieldSelected(selectedText)
                dialog.dismiss()
            }
            .setNegativeButton("Annuleren") { dialog, _ ->
                dialog.cancel()
            }
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(requireContext().getColor(R.color.black))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(requireContext().getColor(R.color.black))
        }
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background_gray)
        return dialog
    }

}