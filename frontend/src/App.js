import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Importação dos componentes de cliente
import ClienteList from './components/cliente/ClienteList';
import ClienteForm from './components/cliente/ClienteForm';
import ClienteDetalhes from './components/cliente/ClienteDetalhes';
import ProdutoList from './components/produtos/ProdutoList';
import ProdutoForm from './components/produtos/ProdutoForm';
import ProdutoDetalhes from './components/produtos/ProdutoDetalhes';
import VendaList from './components/vendas/VendaList';
import VendaDetalhes from './components/vendas/VendaDetalhes';
import VendaForm from './components/vendas/VendaForm';
import OrcamentoForm from './components/orcamentos/OrcamentoForm';
import FornecedorList from './components/fornecedor/FornecedorList';
import FornecedorForm from './components/fornecedor/FornecedorForm';
import FornecedorDetalhes from './components/fornecedor/FornecedorDetalhes'


// Importação do componente Sidebar
import Sidebar from './components/layout/Sidebar';

// Layout Principal
const Layout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <div className="app-container">
      <Sidebar />
      <main className={`main-content ${sidebarOpen ? 'sidebar-open' : ''}`}>
        {children}
      </main>
      <footer className="footer">
        <p>&copy; {new Date().getFullYear()} - Sistema de Gerenciamento</p>
      </footer>
    </div>
  );
};

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          {/* Redireciona a rota raiz para a página de clientes */}
          <Route path="/" element={<Navigate to="/clientes" />} />
          
          {/* Rotas de Clientes */}
          <Route path="/clientes" element={<ClienteList />} />
          <Route path="/clientes/novo" element={<ClienteForm />} />
          <Route path="/clientes/editar/:id" element={<ClienteForm />} />
          <Route path="/clientes/visualizar/:id" element={<ClienteDetalhes />} />
          
          {/* Rotas para Fornecedores (ainda precisam ser implementadas) */}
          <Route path="/fornecedores" element={<FornecedorList/>} />
          <Route path="/fornecedores/novo" element={<FornecedorForm />} />
          <Route path="/fornecedores/editar/:id" element={<FornecedorForm />} />
          <Route path="/fornecedores/visualizar/:id" element={<FornecedorDetalhes />} />
          
          {/* Rotas para Produtos (ainda precisam ser implementadas) */}
          <Route path="/produtos" element={<ProdutoList />} />
          <Route path="/produtos/novo" element={<ProdutoForm/>} />
          <Route path="/produtos/editar/:id" element={<ProdutoForm />} />
          <Route path="/produtos/visualizar/:id" element={<ProdutoDetalhes />} />
          
          {/* Rotas para Vendas (ainda precisam ser implementadas) */}
          <Route path="/vendas" element={<VendaList />} />
          <Route path="/vendas/nova" element={<VendaForm />} />
          <Route path="/vendas/editar/:id" element={<VendaForm />} />
          <Route path="/vendas/visualizar/:id" element={<VendaDetalhes />} />
          
          {/* Rota para Orçamento (ainda precisa ser implementada) */}
          <Route path="/orcamento" element={<OrcamentoForm />} />
          
          {/* Rota para páginas não encontradas */}
          <Route path="*" element={<div className="not-found">Página não encontrada</div>} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
