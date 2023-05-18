package com.example.mywallet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.text.DateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mywallet.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;


public class ExpenseFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference db_expense;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;
    TextView expense_total_result;

    EditText edt_note, edt_type, edt_amount, choosedate;

    ImageButton delete_btn, back_btn;

    Button update_btn;
    Integer amount;

    String type, note, postkey, date;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String uid = firebaseUser.getUid();

        db_expense = FirebaseDatabase.getInstance().getReference().child("Expense data").child(uid);
        expense_total_result = view.findViewById(R.id.expense_value);
        recyclerView = view.findViewById(R.id.recycler_view_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        db_expense.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int expense_total = 0;
                for(DataSnapshot mysnapshot:snapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);
                    expense_total+=data.getAmount();

                    String strExpenseTotal = String.valueOf(expense_total);
                    String expense_total_result_final = new String("-" +strExpenseTotal);
                    expense_total_result.setText(expense_total_result_final);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(db_expense, Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setDate(model.getDate());
                holder.setType(model.getType());
                holder.setAmount(model.getAmount());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey = getRef(holder.getAdapterPosition()).getKey();

                        amount = model.getAmount();
                        type = model.getType();
                        note = model.getNote();
                        date = model.getDate();

                        updateItem();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_item, parent,false));
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View view;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void setType(String type){
            TextView mtype = view.findViewById(R.id.type_txt_expense);
            mtype.setText(type);
        }

        private void  setDate(String date){
            TextView mdate = view.findViewById(R.id.date_txt_expense);
            mdate.setText(date);
        }

        private  void  setAmount(int amount){
            TextView mamount = view.findViewById(R.id.amount_txt_expense);
            String stamount = String.valueOf(amount);
            mamount.setText(stamount);
        }
    }

    private void updateItem(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_data_item, null);

        dialog.setView(view);


        edt_amount = view.findViewById(R.id.edit_amount);
        edt_note = view.findViewById(R.id.edit_note);
        edt_type = view.findViewById(R.id.edit_type);
        choosedate = view.findViewById(R.id.choose_date);
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        edt_type.setText(type);
        edt_type.setSelection(type.length());

        edt_note.setText(note);
        edt_note.setSelection(note.length());

        edt_amount.setText(String.valueOf(amount));
        edt_amount.setSelection(String.valueOf(amount).length());

        update_btn = view.findViewById(R.id.update);
        delete_btn = view.findViewById(R.id.delete);
        back_btn = view.findViewById(R.id.back);

        choosedate.setText(date);
        choosedate.setSelection(date.length());
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
        AlertDialog dialog1 = dialog.create();
        dialog1.show();
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
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

                type = edt_type.getText().toString().trim();

                date = choosedate.getText().toString().trim();

                Data data = new Data(myAmount, type,postkey, note, date);

                db_expense.child(postkey).setValue(data);

                dialog1.dismiss();
            }
        });
        dialog1.show();
    }
}