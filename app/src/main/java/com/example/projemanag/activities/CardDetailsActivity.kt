package com.example.projemanag.activities

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.R
import com.example.projemanag.adapters.CardMemberListItemsAdapter
import com.example.projemanag.dialogs.LabelColorListDialog
import com.example.projemanag.dialogs.MembersListDialog
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.*
import com.example.projemanag.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentExtra()
        setupActionBar()

        findViewById<EditText>(R.id.et_name_card_details).setText(
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)

        findViewById<EditText>(R.id.et_name_card_details).setSelection(findViewById<EditText>(R.id.et_name_card_details).text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        findViewById<AppCompatButton>(R.id.btn_update_card_details).setOnClickListener {
            if(findViewById<EditText>(R.id.et_name_card_details).text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this@CardDetailsActivity, "Enter a card name.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tv_select_label_color).setOnClickListener {
            labelColorsListDialog()
        }

        findViewById<TextView>(R.id.tv_select_members).setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        val toolbarCardDetails = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        setSupportActionBar(toolbarCardDetails)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        toolbarCardDetails.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)


        return super.onCreateOptionsMenu(menu)
    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor() {
        findViewById<TextView>(R.id.tv_select_label_color).text = ""
        findViewById<TextView>(R.id.tv_select_label_color).setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getIntentExtra(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun membersListDialog() {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if(cardAssignedMembersList.size > 0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }else {
            for(i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object: MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member),
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)

                    for(i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id == user.id){
                            mMembersDetailList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()
            }

        }
        listDialog.show()

    }

    private fun updateCardDetails(){
        val card = Card(
            findViewById<EditText>(R.id.et_name_card_details).text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)


        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            // START
            deleteCard()
            // END
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun labelColorsListDialog() {

        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailList.indices){
            for(j in cardAssignedMembersList){
                if(mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(mMembersDetailList[i].id, mMembersDetailList[i].image)
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            findViewById<TextView>(R.id.tv_select_members).visibility = View.GONE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.VISIBLE

            val myAdapter = CardMemberListItemsAdapter(this, selectedMembersList, true)

            findViewById<RecyclerView>(R.id.rv_selected_members_list).apply {
                layoutManager = GridLayoutManager(this@CardDetailsActivity, 6)
                adapter = myAdapter
                myAdapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                })
            }
        }else{
            findViewById<TextView>(R.id.tv_select_members).visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.GONE
        }

    }

}