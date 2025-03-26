import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import vendaService from '../../services/vendaService';
import clienteService from '../../services/clienteService';
import './VendaDetalhes.css';

const VendaDetalhes = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [venda, setVenda] = useState(null);
  const [cliente, setCliente] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchVenda();
  }, [id]);

  const fetchVenda = async () => {
    try {
      setLoading(true);
      const vendaData = await vendaService.getById(id);
      setVenda(vendaData);

      // Fetch cliente details if client ID exists
      if (vendaData.cliente && vendaData.cliente.id) {
        const clienteData = await clienteService.getById(vendaData.cliente.id);
        setCliente(clienteData);
      }

      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados da venda. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Tem certeza que deseja excluir esta venda?')) {
      try {
        await vendaService.delete(id);
        navigate('/vendas');
      } catch (err) {
        setError('Falha ao excluir a venda. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  const handleDownloadPdf = async () => {
    try {
      const pdfBlob = await vendaService.downloadPdf(id);
      const url = window.URL.createObjectURL(pdfBlob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `venda_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError('Falha ao baixar o PDF da venda. Por favor, tente novamente.');
      console.error(err);
    }
  };

  const handleFinalizarVenda = async () => {
    if (window.confirm('Tem certeza que deseja finalizar esta venda?')) {
      try {
        await vendaService.finalizarVenda(id);
        fetchVenda(); // Refresh the data
      } catch (err) {
        setError('Falha ao finalizar a venda. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('pt-BR');
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!venda) return <div className="error">Venda não encontrada.</div>;

  return (
    <div className="venda-detalhes-container">
      <div className="header">
        <h2>Detalhes da Venda</h2>
        <div className="actions">
          <Link to="/vendas" className="btn-back">
            Voltar para Lista
          </Link>
          <Link to={`/vendas/editar/${id}`} className="btn-edit">
            Editar
          </Link>
          <button onClick={handleDelete} className="btn-delete">
            Excluir
          </button>
          <button onClick={handleDownloadPdf} className="btn-pdf">
            Baixar PDF
          </button>
          {!venda.dataVenda && (
            <button onClick={handleFinalizarVenda} className="btn-finalizar">
              Finalizar Venda
            </button>
          )}
        </div>
      </div>

      <div className="venda-card">
        <h3>Venda #{venda.id}</h3>
        <div className="venda-info">
          <div className="info-section">
            <h4>Informações da Venda</h4>
            <div className="info-group">
              <span className="info-label">Data:</span>
              <span className="info-value">
                {venda.dataVenda ? formatDate(venda.dataVenda) : 'Não finalizada'}
              </span>
            </div>
            <div className="info-group">
              <span className="info-label">Valor Total:</span>
              <span className="info-value">{formatCurrency(venda.valorTotal)}</span>
            </div>
            {venda.desconto && venda.desconto.compareTo(0) > 0 && (
              <div className="info-group">
                <span className="info-label">Desconto:</span>
                <span className="info-value">{formatCurrency(venda.desconto)}</span>
              </div>
            )}
            {venda.formaPagamento && (
              <div className="info-group">
                <span className="info-label">Forma de Pagamento:</span>
                <span className="info-value">{venda.formaPagamento}</span>
              </div>
            )}
          </div>

          {cliente && (
            <div className="info-section">
              <h4>Informações do Cliente</h4>
              <div className="info-group">
                <span className="info-label">Nome:</span>
                <span className="info-value">{cliente.nome}</span>
              </div>
              <div className="info-group">
                <span className="info-label">Tipo de Pessoa:</span>
                <span className="info-value">{cliente.tipoPessoa}</span>
              </div>
              {cliente.telefone && (
                <div className="info-group">
                  <span className="info-label">Telefone:</span>
                  <span className="info-value">{cliente.telefone}</span>
                </div>
              )}
            </div>
          )}
        </div>

        <div className="itens-section">
          <h4>Itens da Venda</h4>
          <table className="itens-table">
            <thead>
              <tr>
                <th>Produto</th>
                <th>Quantidade</th>
                <th>Valor Unitário</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {venda.itens.map((item) => (
                <tr key={item.id}>
                  <td>{item.produto.nome}</td>
                  <td>{item.quantidade}</td>
                  <td>{formatCurrency(item.valorUnitario)}</td>
                  <td>{formatCurrency(item.subtotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {venda.observacoes && (
          <div className="observacao-section">
            <h4>Observações</h4>
            <p>{venda.observacoes}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default VendaDetalhes;