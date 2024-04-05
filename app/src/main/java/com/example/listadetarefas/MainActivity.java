package com.example.listadetarefas;

import  androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private ListView listViewListaTarefas;
    private ArrayAdapter<Tarefa> adapter;
    private FireBaseApi fireBaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        listViewListaTarefas = findViewById(R.id.listViewListaTarefas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fireBaseApi = new FireBaseApi(this, listViewListaTarefas, adapter);
        fireBaseApi.buscaTarefas();

        configuracaoClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_abrir_tela) {
            Intent telaCadastro = new Intent(this, CriarTarefaActivity.class);
            startActivity(telaCadastro);
        }
        return super.onOptionsItemSelected(item);
    }

    private void verDetalhes(Tarefa tarefa) {
        String titulo = String.format("Tarefa %s", tarefa.getTitulo());
        String descricao = String.format("Detalhes \n\n%s", tarefa.getDescricao());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(descricao);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configuracaoClick() {
        listViewListaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tarefa t = fireBaseApi.getTarefa(position);
                verDetalhes(t);
            }
        });

        listViewListaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Tarefa t = fireBaseApi.getTarefa(position);
                removerTarefa(t);
                return true;
            }
        });

    }

    private void removerTarefa(Tarefa tarefa) {
        String msg = String.format("A tarefa %s será removida, deseja continuar?", tarefa.getTitulo());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remover tarefa?");
        builder.setMessage(msg);

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fireBaseApi.removerTarefa(tarefa, "Tarefa removida com sucesso!");
                fireBaseApi.buscaTarefas();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}