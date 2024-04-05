package com.example.listadetarefas;

import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
public class FireBaseApi {

    private static final String TABELA_NOME = "/listadetarefas";
    private final Activity activity;
    private ListView listViewTarefas;
    private ArrayAdapter<Tarefa> adapter;
    private List<Tarefa> tarefas;

    public FireBaseApi(Activity activity, ListView listViewTarefas, ArrayAdapter<Tarefa> adapter) {
        this.activity = activity;
        this.listViewTarefas = listViewTarefas;
        this.adapter = adapter;
    }

    public FireBaseApi(Activity activity) {
        this.activity = activity;
    }

    public void criarTarefa(Tarefa tarefa, String message) {
        FirebaseFirestore.getInstance().collection(TABELA_NOME).add(tarefa).addOnSuccessListener(documentReference -> {
           tarefa.setId(documentReference.getId());
           atualizarId(tarefa);

            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            activity.finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(activity.getApplicationContext(), "Erro ao criar tarefa", Toast.LENGTH_LONG).show();
        });
    }

    public Tarefa getTarefa(int posicao) {
        return tarefas.get(posicao);
    }

    public void removerTarefa(Tarefa tarefa, String message) {
        FirebaseFirestore.getInstance().collection(TABELA_NOME).document(tarefa.getId()).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(
                    activity.getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }).addOnFailureListener(aVoid -> {
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Erro ao remover tarefa",
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    private void atualizarId(Tarefa tarefa) {
        FirebaseFirestore.getInstance().collection(TABELA_NOME).document(tarefa.getId()).set(tarefa).addOnSuccessListener(aVoid -> {});
    }

    public void buscaTarefas() {
        tarefas = new ArrayList<>();

        FirebaseFirestore.getInstance().collection(TABELA_NOME).addSnapshotListener((value, error) -> {
            List<DocumentChange> dcs = value.getDocumentChanges();

            for (DocumentChange doc: dcs) {
                if (doc.getType() == DocumentChange.Type.ADDED) {
                    Tarefa t = doc.getDocument().toObject(Tarefa.class);
                    tarefas.add(t);
                }
            }
            adapter = new ArrayAdapter<>(activity.getApplication(), android.R.layout.simple_list_item_1,tarefas);
            listViewTarefas.setAdapter(adapter);
        });
    }
}
