package com.example.usuario.todolistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText texto;
    private ListView lista;
    private Button botao;

    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<Integer> ids;
    private ArrayList<String> itens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texto = (EditText) findViewById(R.id.texto);
        lista = (ListView) findViewById(R.id.lista);
        botao = (Button) findViewById(R.id.botaoSalvar);

        carregaTarefas();
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

               // apagarTarefa(ids.get(position));
                alertaApagaTarefa(position);

                return false;
            }
        });

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 adicionarNovaTarefa(texto.getText().toString());

            }
        });
    }

    private void carregaTarefas(){
        try {
            bancoDados = openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS listatarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

//            String novaTarefa = texto.getText().toString();
//            bancoDados.execSQL("INSERT INTO listatarefas(tarefa) VALUES('" + novaTarefa + "')");

            //Cursor cursor = bancoDados.rawQuery("SELECT * FROM listatarefas", null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM listatarefas ORDER BY id DESC", null);

            int indiceColunaID = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    itens);

            lista.setAdapter(itensAdaptador);


            cursor.moveToFirst();
            while (cursor != null) {
                Log.i("LogX", "ID: " + cursor.getString(indiceColunaID) + " Tarefa: " + cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaID)));
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void apagarTarefa(Integer id){
        try {
            bancoDados.execSQL("DELETE FROM listatarefas WHERE id=" + id);
            Toast.makeText(MainActivity.this, "Tarefa removida!", Toast.LENGTH_SHORT).show();
            carregaTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void adicionarNovaTarefa(String novaTarefa) {
        try {
            if (novaTarefa.equals("")) {
                Toast.makeText(MainActivity.this, "Insira uma tarefa!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Tarefa " + novaTarefa + " salva!", Toast.LENGTH_SHORT).show();
                texto.setText("");
                bancoDados.execSQL("INSERT INTO listatarefas(tarefa) VALUES('" + novaTarefa + "')");
                carregaTarefas();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertaApagaTarefa(Integer idSelecionado){
        String tarefaSelecionada = itens.get(idSelecionado);
        final Integer numeroId = idSelecionado;

        new  AlertDialog.Builder(MainActivity.this)
                .setTitle("Atenção")
                .setMessage("Deseja apagar a tarefa: " +tarefaSelecionada + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apagarTarefa(ids.get(numeroId));
                    }
                }).setNegativeButton("Não", null).show();

    }
}






