import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import orcamentoService from '../../services/orcamentoService';
import clienteService from '../../services/clienteService';
import produtoService from '../../services/produtoService';
import './OrcamentoForm.css';

const OrcamentoForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  
  const [formData, setFormData] = useState({
    nomeCliente: '',
    data: new Date().toISOString().split('T')[0],
    itens: [],
    desconto: 0
  });
  
  const [clientes, setClientes] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [produtoSelecionado, setProdutoSelecionado] = useState(null);
  const [quantidadeProduto, setQuantidadeProduto] = useState(1);
  
  const [loading, setLoading] = useState(isEditing);
  const [error, setError] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    fetchInitialData();
  }, []);

  useEffect(() => {
    if (isEditing) {
      fetchOrcamento();
    }
  }, [id]);

  const fetchInitialData = async () => {
    try {
      const [clientesData, produtosData] = await Promise.all([
        clienteService.getAll(),
        produtoService.getAll()
      ]);
      setClientes(clientesData);
      setProdutos(produtosData);
    } catch (err) {
      setError('Falha ao carregar dados iniciais.');
      console.error(err);
    }
  };

  const fetchOrcamento = async () => {
    try {
      setLoading(true);
      const orcamento = await orcamentoService.getById(id);
      setFormData(orcamento);
    } catch (err) {
      setError('Falha ao carregar os dados do orçamento.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdicionarItem = () => {
    if (!produtoSelecionado || quantidadeProduto <= 0) {
      setError('Selecione um produto e insira uma quantidade válida.');
      return;
    }

    const novoItem = {
      descricao: produtoSelecionado.nome,
      valor: produtoSelecionado.valorVenda * quantidadeProduto
    };

    setFormData(prev => ({
      ...prev,
      itens: [...prev.itens, novoItem]
    }));

    // Resetar seleção de produto
    setProdutoSelecionado(null);
    setQuantidadeProduto(1);
  };

  const handleRemoverItem = (index) => {
    setFormData(prev => ({
      ...prev,
      itens: prev.itens.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitted(true);
    setError(null);

    // Validações básicas
    if (!formData.nomeCliente) {
      setError('Selecione um cliente.');
      return;
    }

    if (formData.itens.length === 0) {
      setError('Adicione pelo menos um item ao orçamento.');
      return;
    }

    try {
      setLoading(true);

      if (isEditing) {
        await orcamentoService.update(id, formData);
      } else {
        await orcamentoService.create(formData);
      }
      
      navigate('/orcamentos');
    } catch (err) {
      setError(`Falha ao ${isEditing ? 'atualizar' : 'criar'} o orçamento.`);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const calcularTotal = () => {
    const totalItens = formData.itens.reduce((total, item) => total + item.valor, 0);
    return totalItens - (formData.desconto || 0);
  };

  const gerarPdf = async () => {
    try {
      const pdfBlob = await orcamentoService.gerarPdf(formData);
      const pdfUrl = URL.createObjectURL(pdfBlob);
      window.open(pdfUrl, '_blank');
    } catch (err) {
      setError('Falha ao gerar o PDF do orçamento.');
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;

  return (
    <div className="orcamento-form-container">
      <h2>{isEditing ? 'Editar Orçamento' : 'Novo Orçamento'}</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="orcamento-form">
        <div className="form-group">
          <label>Cliente*</label>
          <select 
            value={formData.nomeCliente} 
            onChange={(e) => setFormData(prev => ({
              ...prev, 
              nomeCliente: e.target.value
            }))}
            required
          >
            <option value="">Selecione um cliente</option>
            {clientes.map(cliente => (
              <option key={cliente.id} value={cliente.nome}>
                {cliente.nome}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Data</label>
          <input 
            type="date" 
            value={formData.data} 
            onChange={(e) => setFormData(prev => ({
              ...prev, 
              data: e.target.value
            }))}
          />
        </div>

        <div className="itens-orcamento-section">
          <h3>Itens do Orçamento</h3>
          <div className="adicionar-item">
            <select 
              value={produtoSelecionado?.id || ''}
              onChange={(e) => {
                const produto = produtos.find(p => p.id === Number(e.target.value));
                setProdutoSelecionado(produto);
              }}
            >
              <option value="">Selecione um produto</option>
              {produtos.map(produto => (
                <option key={produto.id} value={produto.id}>
                  {produto.nome} - {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(produto.valorVenda)}
                </option>
              ))}
            </select>
            <input 
              type="number" 
              min="1" 
              value={quantidadeProduto} 
              onChange={(e) => setQuantidadeProduto(Number(e.target.value))}
            />
            <button type="button" onClick={handleAdicionarItem}>
              Adicionar Item
            </button>
          </div>

          <table className="itens-table">
            <thead>
              <tr>
                <th>Descrição</th>
                <th>Valor</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {formData.itens.map((item, index) => (
                <tr key={index}>
                  <td>{item.descricao}</td>
                  <td>{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.valor)}</td>
                  <td>
                    <button type="button" onClick={() => handleRemoverItem(index)}>
                      Remover
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="form-group">
          <label>Desconto</label>
          <input 
            type="number" 
            value={formData.desconto || 0} 
            onChange={(e) => setFormData(prev => ({
              ...prev, 
              desconto: Number(e.target.value)
            }))}
            step="0.01" 
            min="0"
          />
        </div>

        <div className="total-orcamento">
          <strong>Total do Orçamento: </strong>
          {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(calcularTotal())}
        </div>

        <div className="form-actions">
          <button 
            type="button" 
            onClick={() => navigate('/orcamentos')} 
            className="btn-cancel"
          >
            Cancelar
          </button>
          <button 
            type="button" 
            onClick={gerarPdf} 
            className="btn-pdf"
            disabled={!formData.itens.length}
          >
            Gerar PDF
          </button>
        </div>
      </form>
    </div>
  );
};

export default OrcamentoForm;