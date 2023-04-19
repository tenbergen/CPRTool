import React from 'react'
import { Link, useLocation } from 'react-router-dom'

const Breadcrumbs = () => {
  const location = useLocation()
  const paths = location.pathname.split('/').filter((path) => path)

  return (
    <div>
      {paths.map((path, i) => {
        const url = `/${paths.slice(0, i + 1).join('/')}`
        let text = path.charAt(0).toUpperCase() + path.slice(1)
        text = text.replace('%20', ' ')

        return (
          <span key={url}>
            {i === paths.length - 1 ? <Link to={url} style={{ fontWeight: 'bold' }}>{text}</Link> : <Link
              to={url}>{text}</Link>}
            {i < paths.length - 1 && <span> ⏵ </span>}
          </span>
        )
      })}
    </div>
  )
}

export default Breadcrumbs
