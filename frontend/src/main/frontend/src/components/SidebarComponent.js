//import useState hook to create menu collapse state
import React  from 'react';

//import react pro sidebar components
import {
  ProSidebar,
  Menu,
  MenuItem,
  SidebarFooter,
  SidebarContent,
} from 'react-pro-sidebar';

//import icons from react icons
import { FaList } from 'react-icons/fa';
import {
  FiLogOut,
} from 'react-icons/fi';

//import sidebar css from react-pro-sidebar module and our custom css
import 'react-pro-sidebar/dist/css/styles.css';
import './styles/Sidebar.css';

const handleLogout = () => {
  localStorage.clear();
}

const logoutURL = `${process.env.REACT_APP_URL}/logout`
const SidebarComponent = () => {
  return (
    <>
      <div id='sidebar'>
        <ProSidebar>
          <SidebarContent>
            <Menu iconShape='circle'>
              <MenuItem icon={<FaList />}>Courses</MenuItem>
            </Menu>
          </SidebarContent>
          <SidebarFooter>
            <a href={logoutURL} onClick={handleLogout}>
              <Menu iconShape='circle'>
                <MenuItem icon={<FiLogOut />}>Logout</MenuItem>
              </Menu>
            </a>
          </SidebarFooter>
        </ProSidebar>
      </div>
    </>
  );
};

export default SidebarComponent;
