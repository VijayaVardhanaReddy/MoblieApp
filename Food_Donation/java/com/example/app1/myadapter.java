package com.example.app1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder> {

    ArrayList<model> datalist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public myadapter(ArrayList<model> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        model data = datalist.get(position);
        holder.tname.setText(data.getName());
        holder.ttype.setText(data.getType());
        holder.tdescription.setText(data.getDescription());

        // Set delete button functionality
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use holder.getAdapterPosition() to get the correct position dynamically
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) { // Check if position is valid
                    model dataToDelete = datalist.get(currentPosition);
                    String documentId = dataToDelete.getDocumentId();

                    if (documentId != null) {
                        db.collection("user data").document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Remove item from the list dynamically using current position
                                    datalist.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    notifyItemRangeChanged(currentPosition, datalist.size());
                                    Toast.makeText(v.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(v.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView tname, ttype, tdescription;
        Button deleteBtn;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tname = itemView.findViewById(R.id.name);
            ttype = itemView.findViewById(R.id.type);
            tdescription = itemView.findViewById(R.id.description);
            deleteBtn = itemView.findViewById(R.id.delete_button);
        }
    }
}



//public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder>
//{
//    ArrayList<model> datalist;
//    FirebaseAuth fAuth= FirebaseAuth.getInstance();
//    public String userID = fAuth.getCurrentUser().getUid();
//    public String uid;
//
//
//    public myadapter(ArrayList<model> datalist) {
//        this.datalist = datalist;
//    }
//
//    @NonNull
//    @Override
//    public myviewholder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
//        return new myviewholder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
//        holder.tname.setText(datalist.get(position).getName());
//        holder.ttype.setText(datalist.get(position).getType());
//        holder.tdescription.setText(datalist.get(position).getDescription());
//    }
//
//    public void deleteItem(int position){
//        //getSnapshots().getSnapshot(position).getReference().delete();
//        //notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemCount() {
//        return datalist.size();
//    }
//
//    class myviewholder extends RecyclerView.ViewHolder
//    {
//        TextView tname,ttype,tdescription;
//        public myviewholder(@NonNull View itemView) {
//            super(itemView);
//            tname = itemView.findViewById(R.id.name);
//            ttype = itemView.findViewById(R.id.type);
//            tdescription = itemView.findViewById(R.id.description);
//        }
//    }
//}
