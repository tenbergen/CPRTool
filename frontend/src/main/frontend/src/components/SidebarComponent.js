//import useState hook to create menu collapse state
import React, { useState } from 'react';

//import react pro sidebar components
import {
  ProSidebar,
  Menu,
  MenuItem,
  SidebarHeader,
  SidebarFooter,
  SidebarContent,
} from 'react-pro-sidebar';

//import icons from react icons
import { FaList, FaRegHeart } from 'react-icons/fa';
import {
  FiHome,
  FiLogOut,
  FiArrowLeftCircle,
  FiArrowRightCircle,
} from 'react-icons/fi';
import { RiPencilLine } from 'react-icons/ri';
import { BiCog } from 'react-icons/bi';

//import sidebar css from react-pro-sidebar module and our custom css
import 'react-pro-sidebar/dist/css/styles.css';
import './styles/Sidebar.css';

const handleLogout = () => {
  localStorage.clear();
}

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
            <a href='http://moxie.cs.oswego.edu:13126/logout' onClick={handleLogout}>
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
