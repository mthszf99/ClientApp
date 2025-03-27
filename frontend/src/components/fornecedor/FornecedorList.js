import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import fornecedorService from '../../services/fornecedorService';
import './FornecedorList.css';

const FornecedorList = () => {
  const [fornecedores, setFornecedores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchFornecedores();
  }, []);

  const fetchFornecedores = async () => {
    try {
      setLoading(true);
      const data = await fornecedorService.getAll();
      setFornecedores(data);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os fornecedores. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este fornecedor?')) {
      try {
        await fornecedorService.delete(id);
        setFornecedores(fornecedores.filter(fornecedor => fornecedor.id !== id));
      } catch (err) {
        setError('Falha ao excluir o fornecedor. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="cliente-list-container">
      <div className="header">
        <h2>Fornecedores</h2>
        <Link to="/fornecedores/novo" className="btn-novo">Novo Fornecedor</Link>
      </div>
      
      {fornecedores.length === 0 ? (
        <div className="no-records">Nenhum fornecedor cadastrado.</div>
      ) : (
        <div className="table-responsive">
          <table className="cliente-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>CPF/CNPJ</th>
                <th>Telefone</th>
                <th>Email</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {fornecedores.map(fornecedor => (
                <tr key={fornecedor.id}>
                  <td>{fornecedor.nome}</td>
                  <td>{fornecedor.cpfCnpj}</td>
                  <td>{fornecedor.telefone}</td>
                  <td>{fornecedor.email}</td>
                  <td className="actions">
                    <Link to={`/fornecedores/visualizar/${fornecedor.id}`} className="btn-view">
                      Visualizar
                    </Link>
                    <Link to={`/fornecedores/editar/${fornecedor.id}`} className="btn-edit">
                      Editar
                    </Link>
                    <button 
                      onClick={() => handleDelete(fornecedor.id)} 
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

export default FornecedorList;