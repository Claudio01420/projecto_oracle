// App.js
import React, { useState, useEffect } from 'react';
import NewItem from './NewItem';
import API_LIST from './API';
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, TableBody, CircularProgress } from '@mui/material';
import Moment from 'react-moment';
import 'moment/locale/es';

function App() {
  const [isLoading, setLoading] = useState(false);
  const [isInserting, setInserting] = useState(false);
  const [items, setItems] = useState([]);
  const [error, setError] = useState();

  const pendientes = items.filter(x => !x.done);
  const hechas = items.filter(x => x.done);

  const fetchData = () => {
    setLoading(true);
    fetch(API_LIST)
      .then((response) => response.json())
      .then((data) => {
        setItems(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchData();
  }, []);

  const addItem = (description) => {
    setInserting(true);
    fetch(API_LIST, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ description }),
    })
      .then(() => {
        fetchData();
        setInserting(false);
      })
      .catch((err) => {
        setError(err);
        setInserting(false);
      });
  };

  const deleteItem = (id) => {
    fetch(`${API_LIST}/${id}`, { method: 'DELETE' })
      .then(() => fetchData())
      .catch((err) => setError(err));
  };

  const toggleDone = (event, id, description, done) => {
    fetch(`${API_LIST}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ description, done }),
    })
      .then(() => fetchData())
      .catch((err) => setError(err));
  };

  return (
    <div className="App">
      <h1>📝 Mi Lista de Tareas</h1>
      <NewItem addItem={addItem} isInserting={isInserting} />
      {error && <p className="error">Error: {error.message}</p>}
      {isLoading && <CircularProgress />}
      {!isLoading && (
        <div id="maincontent">
          <h2>Pendientes ({pendientes.length})</h2>
          {pendientes.length === 0 && (
            <div className="empty">No tienes tareas pendientes. ¡Agrega una! ✨</div>
          )}
          <table id="itemlistNotDone" className="itemlist">
            <TableBody>
              {pendientes.map((item) => (
                <tr key={item.id}>
                  <td className="description">{item.description}</td>
                  <td className="date">
                    <Moment locale="es" format="D MMM, HH:mm:ss">
                      {item.createdAt}
                    </Moment>
                  </td>
                  <td>
                    <Button
                      variant="contained"
                      className="DoneButton"
                      onClick={(event) =>
                        toggleDone(event, item.id, item.description, !item.done)
                      }
                      size="small"
                    >
                      Hecho
                    </Button>
                  </td>
                </tr>
              ))}
            </TableBody>
          </table>

          <h2 id="donelist">Completadas ({hechas.length})</h2>
          {hechas.length === 0 && (
            <div className="empty">Aún no completas tareas. ¡Tú puedes! 💪</div>
          )}
          <table id="itemlistDone" className="itemlist">
            <TableBody>
              {hechas.map((item) => (
                <tr key={item.id}>
                  <td className="description">{item.description}</td>
                  <td className="date">
                    <Moment locale="es" format="D MMM, HH:mm:ss">
                      {item.createdAt}
                    </Moment>
                  </td>
                  <td>
                    <Button
                      variant="contained"
                      className="DoneButton"
                      onClick={(event) =>
                        toggleDone(event, item.id, item.description, !item.done)
                      }
                      size="small"
                    >
                      Deshacer
                    </Button>
                  </td>
                  <td>
                    <Button
                      startIcon={<DeleteIcon />}
                      variant="contained"
                      className="DeleteButton"
                      onClick={() => deleteItem(item.id)}
                      size="small"
                    >
                      Eliminar
                    </Button>
                  </td>
                </tr>
              ))}
            </TableBody>
          </table>
        </div>
      )}
    </div>
  );
}

export default App;