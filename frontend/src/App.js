import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Importação dos componentes de cliente
import ClienteList from './components/cliente/ClienteList';
import ClienteForm from './components/cliente/ClienteForm';
import ClienteDetalhes from './components/cliente/ClienteDetalhes';

// Componente de Navegação
const NavBar = () => {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <a href="/" className="navbar-logo">
          Sistema de Clientes
        </a>
        <div className="nav-menu">
          <a href="/clientes" className="nav-links">Clientes</a>
          {/* Adicione mais links de navegação conforme necessário */}
        </div>
      </div>
    </nav>
  );
};

// Layout Principal
const Layout = ({ children }) => {
  return (
    <div className="app-container">
      <NavBar />
      <main className="main-content">
        {children}
      </main>
      <footer className="footer">
        <p>&copy; {new Date().getFullYear()} - Sistema de Gerenciamento de Clientes</p>
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
          
          {/* Adicione mais rotas conforme necessário */}
          
          {/* Rota para páginas não encontradas */}
          <Route path="*" element={<div className="not-found">Página não encontrada</div>} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
