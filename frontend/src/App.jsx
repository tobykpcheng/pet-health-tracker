import { Route, Routes } from 'react-router'
import Header from './components/Header.jsx'
import PetListPage from './pages/PetListPage.jsx'
import AddPetPage from './pages/AddPetPage.jsx'

// Each <Route> maps a URL path to a page component.
export default function App() {
  return (
    <>
      <Header />
      <main>
        <Routes>
          <Route path="/" element={<PetListPage />} />
          <Route path="/pets/new" element={<AddPetPage />} />
        </Routes>
      </main>
    </>
  )
}
