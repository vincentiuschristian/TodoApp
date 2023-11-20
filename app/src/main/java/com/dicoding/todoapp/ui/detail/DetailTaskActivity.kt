package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var detailTaskViewModel: DetailTaskViewModel
    private lateinit var edTitle: TextInputEditText
    private lateinit var edtDesc: TextInputEditText
    private lateinit var edtDueDate: TextInputEditText
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        edTitle = findViewById(R.id.detail_ed_title)
        edtDesc = findViewById(R.id.detail_ed_description)
        edtDueDate = findViewById(R.id.detail_ed_due_date)
        btnDelete = findViewById(R.id.btn_delete_task)

        //TODO 11 : Show detail task and implement delete action
        val taskId = intent.getIntExtra(TASK_ID, 0)
        detailTaskViewModel.setTaskId(taskId)

        detailTaskViewModel.task.observe(this) { task ->
            if (task != null) {
                edTitle.setText(task.title)
                edtDesc.setText(task.description)
                edtDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
            }
        }

        btnDelete.setOnClickListener {
            detailTaskViewModel.deleteTask()
            finish()
        }

    }
}