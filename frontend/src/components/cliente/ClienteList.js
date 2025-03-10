import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import clienteService from '../../services/clienteService';
import './ClienteList.css';

const ClienteList = () => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchClientes();
  }, []);

  const fetchClientes = async () => {
    try {
      setLoading(true);
      const data = await clienteService.getAll();
      setClientes(data);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os clientes. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await clienteService.delete(id);
        setClientes(clientes.filter(cliente => cliente.id !== id));
      } catch (err) {
        setError('Falha ao excluir o cliente. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="cliente-list-container">
      <div className="header">
        <h2>Clientes</h2>
        <Link to="/clientes/novo" className="btn-novo">Novo Cliente</Link>
      </div>
      
      {clientes.length === 0 ? (
        <div className="no-records">Nenhum cliente cadastrado.</div>
      ) : (
        <div className="table-responsive">
          <table className="cliente-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Tipo</th>
                <th>Telefone</th>
                <th>Email</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {clientes.map(cliente => (
                <tr key={cliente.id}>
                  <td>{cliente.nome}</td>
                  <td>{cliente.tipoPessoa}</td>
                  <td>{cliente.telefone}</td>
                  <td>{cliente.email}</td>
                  <td className="actions">
                    <Link to={`/clientes/visualizar/${cliente.id}`} className="btn-view">
                      Visualizar
                    </Link>
                    <Link to={`/clientes/editar/${cliente.id}`} className="btn-edit">
                      Editar
                    </Link>
                    <button 
                      onClick={() => handleDelete(cliente.id)} 
                      className="btn-delete"
                    >
                      Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ClienteList;