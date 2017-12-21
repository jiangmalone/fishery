

function NavBar({title}) {
    return <div className="nav-bar-title">
        <i className="back" onClick={()=>{history.back()}}></i>
        {title}
        <i className="scan"></i>
    </div>
}
export default NavBar