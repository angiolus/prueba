package c.example.ouiy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PymeAdapter extends RecyclerView.Adapter<PymeAdapter.PymeViewHolder> {

    private List<Pyme> pymeList;

    public PymeAdapter(List<Pyme> pymeList) {
        this.pymeList = pymeList;
    }

    @NonNull
    @Override
    public PymeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pyme_item, parent, false);
        return new PymeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PymeViewHolder holder, int position) {
        Pyme pyme = pymeList.get(position);
        holder.nameTextView.setText(pyme.getName());
        holder.emailTextView.setText(pyme.getEmail());
    }

    @Override
    public int getItemCount() {
        return pymeList.size();
    }

    static class PymeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;

        public PymeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.pymeNameTextView);
            emailTextView = itemView.findViewById(R.id.pymeEmailTextView);
        }
    }
}
