package com.example.mywallet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywallet.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class ListFragment extends Fragment {

    FloatingActionButton floatingActionButton_main, floatingActionButton_income, floatingActionButton_expense;
    TextView fab_income_txt, fab_expense_txt;

    boolean isOpen=false;
    Animation fabOpen, fabClose;

    FirebaseAuth auth;

    FirebaseRecyclerAdapter adapterIncome, adapterExpense;

    RecyclerView recyclerView_income, recyclerView_expense;

    DatabaseReference db_income, db_expense;

    TextView income_total_value, expense_total_value, get_user, insert_choosedate;
    EditText etd_type, edt_note, edt_amount, choosedate;

    Button update_btn;

    ImageButton del_btn, cancle_btn;

    String note, type, postkey, date;

    Integer amount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        get_user = view.findViewById(R.id.user);
        showUserInfo();
        db_income = FirebaseDatabase.getInstance().getReference().child("Income data").child(uid);
        db_expense = FirebaseDatabase.getInstance().getReference().child("Expense data").child(uid);
        floatingActionButton_main = view.findViewById(R.id.main_button);
        floatingActionButton_income = view.findViewById(R.id.income_btn);
        floatingActionButton_expense = view.findViewById(R.id.expense_btn);

        fab_expense_txt = view.findViewById(R.id.expense_txt);
        fab_income_txt = view.findViewById(R.id.income_txt);

        income_total_value = view.findViewById(R.id.value_income);
        expense_total_value = view.findViewById(R.id.value_expense);

        recyclerView_income = view.findViewById(R.id.recycler_view_income_list);
        recyclerView_expense = view.findViewById(R.id.recycler_view_expense_list);

        fabOpen= AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);



        floatingActionButton_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                if(isOpen){
                    floatingActionButton_income.startAnimation(fabClose);
                    floatingActionButton_expense.startAnimation(fabClose);
                    floatingActionButton_income.setClickable(false);
                    floatingActionButton_expense.setClickable(false);

                    fab_expense_txt.startAnimation(fabClose);
                    fab_income_txt.startAnimation(fabClose);

                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }else {
                    floatingActionButton_income.startAnimation(fabOpen);
                    floatingActionButton_expense.startAnimation(fabOpen);
                    floatingActionButton_income.setClickable(true);
                    floatingActionButton_expense.setClickable(true);

                    fab_income_txt.startAnimation(fabOpen);
                    fab_expense_txt.startAnimation(fabOpen);

                    fab_expense_txt.setClickable(true);
                    fab_income_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });

         db_income.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 int income_total = 0;
                 for(DataSnapshot mysnapshot:snapshot.getChildren()){
                     Data data = mysnapshot.getValue(Data.class);
                     income_total+=data.getAmount();
                     String strAmountTotal = String.valueOf(income_total);
                     String income_total_final = new String("+" + strAmountTotal);
                     income_total_value.setText(income_total_final);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         db_expense.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 int expense_total = 0;
                 for(DataSnapshot mysnapshot:snapshot.getChildren()){
                     Data data = mysnapshot.getValue(Data.class);
                     expense_total+=data.getAmount();
                     String strExpenseTotal = String.valueOf(expense_total);
                     String expense_total_final = new String("-" + strExpenseTotal);
                     expense_total_value.setText(expense_total_final);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

         //Recycler

        LinearLayoutManager layoutManager_Income = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager_Income.setStackFromEnd(true);
        layoutManager_Income.setReverseLayout(true);
        recyclerView_income.setLayoutManager(layoutManager_Income);

        LinearLayoutManager layoutManager_Expense = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager_Expense.setStackFromEnd(true);
        layoutManager_Expense.setReverseLayout(true);
        recyclerView_expense.setLayoutManager(layoutManager_Expense);

        return view;
    }

    private void insertData(){
        floatingActionButton_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.custom_layout_insert, null);
                mydialog.setView(view);
                AlertDialog dialog = mydialog.create();

                EditText edtAmount = view.findViewById(R.id.amount);
                EditText edtType = view.findViewById(R.id.type);
                EditText edtNote = view.findViewById(R.id.note);
                Button btnCancle = view.findViewById(R.id.cancel);
                Button btnSave = view.findViewById(R.id.save);
                TextView insert_choosedate = view.findViewById(R.id.ichoose_date);

                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                insert_choosedate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month+1;
                                String date = dayOfMonth+"/"+month+"/"+year;
                                insert_choosedate.setText(date);
                            }
                        }, year,month,day);
                        datePickerDialog.show();
                    }
                });
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = edtType.getText().toString().trim();
                        String amount = edtAmount.getText().toString().trim();
                        String note = edtNote.getText().toString().trim();
                        String date = insert_choosedate.getText().toString().trim();

                        if(TextUtils.isEmpty(type)){
                            edtType.setError("Please enter type!");
                            return;
                        }
                        if(TextUtils.isEmpty(amount)){
                            edtAmount.setError("Please enter amount!");
                            return;
                        }
                        int ourammontint = Integer.parseInt(amount);
                        if(TextUtils.isEmpty(note)){
                            edtNote.setError("Please enter note!");
                            return;
                        }

                        String id = db_income.push().getKey();

//                        String date = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(ourammontint,type, id, note, date);

                        db_income.child(id).setValue(data);

                        Toast.makeText(getActivity(), "Data added", Toast.LENGTH_LONG).show();

                        dialog.dismiss();

                    }
                });

                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
        floatingActionButton_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.custom_layout_insert, null);
                mydialog.setView(view);
                AlertDialog dialog = mydialog.create();

                EditText edtAmount = view.findViewById(R.id.amount);
                EditText edtType = view.findViewById(R.id.type);
                EditText edtNote = view.findViewById(R.id.note);
                Button btnCancle = view.findViewById(R.id.cancel);
                Button btnSave = view.findViewById(R.id.save);
                TextView insert_choosedate = view.findViewById(R.id.ichoose_date);

                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                insert_choosedate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month+1;
                                String date = day+"/"+month+"/"+year;
                                insert_choosedate.setText(date);
                            }
                        }, year, month,day);
                        datePickerDialog.show();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = edtType.getText().toString().trim();
                        String amount = edtAmount.getText().toString().trim();
                        String note = edtNote.getText().toString().trim();
                        String date = insert_choosedate.getText().toString().trim();

                        if(TextUtils.isEmpty(type)){
                            edtType.setError("Please enter type!");
                            return;
                        }
                        if(TextUtils.isEmpty(amount)){
                            edtAmount.setError("Please enter amount!");
                            return;
                        }
                        int ourammontint = Integer.parseInt(amount);
                        if(TextUtils.isEmpty(note)){
                            edtNote.setError("Please enter note!");
                            return;
                        }

                        String id = db_expense.push().getKey();

                        Data data = new Data(ourammontint,type, id, note, date);

                        db_expense.child(id).setValue(data);

                        Toast.makeText(getActivity(), "Data added", Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });

                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options_income = new FirebaseRecyclerOptions.Builder<Data>().setQuery(db_income.limitToLast(3), Data.class).build();
        FirebaseRecyclerOptions<Data> options_expense = new FirebaseRecyclerOptions.Builder<Data>().setQuery(db_expense.limitToLast(3), Data.class).build();

        adapterExpense = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options_expense) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseDate(model.getDate());
                holder.setExpenseType(model.getType());
                holder.setExpenseAmount(model.getAmount());

                holder.expenseView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey = getRef(holder.getAdapterPosition()).getKey();

                        amount = model.getAmount();
                        type = model.getType();
                        note = model.getNote();
                        date = model.getDate();
                        updateExpenseItem();
                    }
                });
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense_ritem, parent,false));
            }
        };
        adapterExpense.startListening();
        recyclerView_expense.setAdapter(adapterExpense);

        adapterIncome = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options_income) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());
                holder.setIncomeAmount(model.getAmount());

                holder.incomeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey = getRef(holder.getAdapterPosition()).getKey();

                        amount = model.getAmount();
                        type = model.getType();
                        note = model.getNote();
                        date = model.getDate();
                        updateIncomeItem();

                    }
                });
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboad_income_ritem, parent,false));
            }
        };

        adapterIncome.startListening();
        recyclerView_income.setAdapter(adapterIncome);

    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View expenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseView = itemView;
        }

        public void setExpenseType(String type){
            TextView expense_type = expenseView.findViewById(R.id.db_type_txt_expense);
            expense_type.setText(type);

        }

        public void setExpenseDate(String date){
            TextView expense_date = expenseView.findViewById(R.id.db_date_txt_expense);
            expense_date.setText(date);
        }

        public void setExpenseAmount(Integer amount){
            TextView expense_amount = expenseView.findViewById(R.id.db_amount_txt_expense);
            String str_expense_amount = String.valueOf(amount);
            expense_amount.setText(str_expense_amount);
        }
    }
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View incomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeView = itemView;
        }

        public void setIncomeType(String type){
            TextView income_type = incomeView.findViewById(R.id.db_type_txt_income);
            income_type.setText(type);
        }

        public void setIncomeDate(String date){
            TextView income_date = incomeView.findViewById(R.id.db_date_txt_income);
            income_date.setText(date);
        }

        public void setIncomeAmount(Integer amount){
            TextView income_amount = incomeView.findViewById(R.id.db_amount_txt_income);
            String strIncomeAmount = String.valueOf(amount);

            income_amount.setText(strIncomeAmount);
        }

    }

    private void updateIncomeItem(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_data_item, null);

        dialog.setView(view);

        edt_amount = view.findViewById(R.id.edit_amount);
        edt_note = view.findViewById(R.id.edit_note);
        etd_type = view.findViewById(R.id.edit_type);

        choosedate = view.findViewById(R.id.choose_date);
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etd_type.setText(type);
        etd_type.setSelection(type.length());
        edt_note.setText(note);
        edt_note.setSelection(note.length());
        edt_amount.setText(String.valueOf(amount));
        edt_amount.setSelection(String.valueOf(amount).length());

        choosedate.setText(date);
//        choosedate.setSelection(date.length());
        choosedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        choosedate.setText(date);

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        del_btn = view.findViewById(R.id.delete);
        update_btn = view.findViewById(R.id.update);
        cancle_btn = view.findViewById(R.id.back);

        AlertDialog dialog1 = dialog.create();
        dialog1.show();
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog1.dismiss();
            }
        });
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db_income.child(postkey).removeValue();

                dialog1.dismiss();

            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount_tamp = String.valueOf(amount);
                amount_tamp=edt_amount.getText().toString().trim();

                int myAmount = Integer.parseInt(amount_tamp);

                note = edt_note.getText().toString().trim();

                type = etd_type.getText().toString().trim();

                date = choosedate.getText().toString().trim();

                Data data = new Data(myAmount, type,postkey, note, date);

                db_income.child(postkey).setValue(data);

                dialog1.dismiss();
            }
        });
        dialog1.show();
    }

    private void updateExpenseItem(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_data_item, null);

        dialog.setView(view);


        edt_amount = view.findViewById(R.id.edit_amount);
        edt_note = view.findViewById(R.id.edit_note);
        etd_type = view.findViewById(R.id.edit_type);
        choosedate = view.findViewById(R.id.choose_date);

        etd_type.setText(type);
        etd_type.setSelection(type.length());

        edt_note.setText(note);
        edt_note.setSelection(note.length());

        edt_amount.setText(String.valueOf(amount));
        edt_amount.setSelection(String.valueOf(amount).length());

        choosedate = view.findViewById(R.id.choose_date);
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        choosedate.setText(date);
        choosedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        choosedate.setText(date);

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        update_btn = view.findViewById(R.id.update);
        del_btn = view.findViewById(R.id.delete);
        cancle_btn = view.findViewById(R.id.back);

        AlertDialog dialog1 = dialog.create();
        dialog1.show();
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db_expense.child(postkey).removeValue();

                dialog1.dismiss();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount_tamp = String.valueOf(amount);
                amount_tamp=edt_amount.getText().toString().trim();

                int myAmount = Integer.parseInt(amount_tamp);

                note = edt_note.getText().toString().trim();

                type = etd_type.getText().toString().trim();

                date = choosedate.getText().toString().trim();

                Data data = new Data(myAmount, type,postkey, note, date);

                db_expense.child(postkey).setValue(data);

                dialog1.dismiss();
            }
        });
        dialog1.show();
    }

    private void showUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String email = user.getEmail();
        String s_email = new String("Hello " + email);
        get_user.setText(s_email);

    }
}