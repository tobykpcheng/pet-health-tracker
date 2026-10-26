import { Link } from 'react-router'

export default function Header() {
  return (
    <header className="header">
      <Link to="/" className="header-title">
        <span aria-hidden="true">🐶🐱</span> PET HEALTH TRACKER
      </Link>
    </header>
  )
}
