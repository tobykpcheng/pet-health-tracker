import { Route, Routes } from 'react-router'
import Header from './components/Header.jsx'
import PetListPage from './pages/PetListPage.jsx'
import AddPetPage from './pages/AddPetPage.jsx'
import PetProfilePage from './pages/PetProfilePage.jsx'
import EditPetPage from './pages/EditPetPage.jsx'

// Each <Route> maps a URL path to a page component. ":id" matches any pet id.
export default function App() {
  return (
    <>
      <Header />
      <main>
        <Routes>
          <Route path="/" element={<PetListPage />} />
          <Route path="/pets/new" element={<AddPetPage />} />
          <Route path="/pets/:id" element={<PetProfilePage />} />
          <Route path="/pets/:id/edit" element={<EditPetPage />} />
        </Routes>
      </main>
    </>
  )
}
