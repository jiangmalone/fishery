

function NavBar({title}) {
    return <div className="nav-bar-title">
        <i className="back" onClick={()=>{history.back()}}></i>
        {title}
        <i className="right-item-none"></i>
    </div>
}
export default NavBar