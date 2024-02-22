package com.example.taskmanager.ui.view

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.work.Data
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.taskmanager.databinding.DateTimeDialogBinding
import com.example.taskmanager.util.NotifyWork
import com.example.taskmanager.util.NotifyWork.Companion.NOTIFICATION_WORK
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit.MILLISECONDS

// TODO: Remove when not needed

@AndroidEntryPoint
class DateTimePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: DateTimeDialogBinding
    private lateinit var checkNotificationPermission: ActivityResultLauncher<String>
    private var isPermission = false
    val customCalendar = Calendar.getInstance()
    val customTime = customCalendar.timeInMillis
    private var year = 2023
    private var month = 2
    private var day = 16
    val c = Calendar.getInstance()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of TimePickerDialog and return it.
        //return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        return DatePickerDialog(requireContext(), this, year, month, day)

        // checkPermission()
//        return activity?.let {
//            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
//            binding = DateTimeDialogBinding.inflate(inflater)
//
//            checkNotificationPermission = registerForActivityResult(
//                ActivityResultContracts.RequestPermission()
//            ) { isGranted: Boolean ->
//                isPermission = isGranted
//            }
//
//            builder.setView(inflater.inflate(R.layout.date_time_dialog, null))
//                .setPositiveButton(R.string.schedule,
//                    DialogInterface.OnClickListener { dialog, id ->
//                        binding.apply {
//                            customCalendar.set(
//                                datePicker.year,
//                                datePicker.month,
//                                datePicker.dayOfMonth,
//                                timePicker.hour,
//                                timePicker.minute,
//                                0
//                            )
//                            //  customCalendar.set(Calendar.YEAR, year)
//                             customCalendar.set(Calendar.DAY_OF_MONTH, day)
//                            datePicker.setOnDateChangedListener { datePicker, i, i2, i3 ->
//                                customCalendar.set(
//                                    i,
//                                    i2,
//                                    i3,
//                                )
//                            }
//                            Log.e("delay", customCalendar.get(1).toString())
//                            val customT = customCalendar.timeInMillis
//                            Log.e("delay", getDate(customT, "dd/MM/yyyy hh:mm:ss.SSS"))
//                        }
//
//
//                        val currentTime = currentTimeMillis()
//                        if (customTime > currentTime) {
//                            val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
//                            val delay = customTime - currentTime
//                            Log.e("delay", "Delay: $delay")
//                            scheduleNotification(delay, data)
//
//                            val titleNotificationSchedule =
//                                getString(R.string.notification_schedule_title)
//                            val patternNotificationSchedule =
//                                getString(R.string.notification_schedule_pattern)
//                            Toast.makeText(
//                                context, titleNotificationSchedule + SimpleDateFormat(
//                                    patternNotificationSchedule, getDefault()
//                                ).format(customCalendar.time).toString(), Toast.LENGTH_LONG
//                            ).show()
//                        } else {
//                            val errorNotificationSchedule =
//                                getString(R.string.notification_schedule_error)
//                            //Toast.makeText(context, errorNotificationSchedule, Toast.LENGTH_LONG).show()
//                        }
//                    })
//                .setNegativeButton(R.string.cancel,
//                    DialogInterface.OnClickListener { dialog, id ->
//                        getDialog()?.cancel()
//                    })
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePicker.setOnDateChangedListener { datePicker, i, i2, i3 ->
            customCalendar.set(
                i,
                i2,
                i3,
            )
        }

    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false

                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date the user picks.
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)

        var customT = c.timeInMillis
        Log.e("delay", getDate(customT, "dd/MM/yyyy hh:mm:ss.SSS"))
        Toast.makeText(context, getDate(customT, "dd/MM/yyyy hh:mm:ss.SSS"), Toast.LENGTH_LONG)
            .show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time the user picks.
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        var customT = c.timeInMillis
        Log.e("delay", getDate(customT, "dd/MM/yyyy hh:mm:ss.SSS"))
        Toast.makeText(context, getDate(customT, "dd/MM/yyyy hh:mm:ss.SSS"), Toast.LENGTH_LONG)
            .show()
    }

}