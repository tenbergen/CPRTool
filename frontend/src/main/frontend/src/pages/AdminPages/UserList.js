import React from 'react';

function UserList({ userList }) {
  return (
    <div className='user-list'>
      <div className='user-item header'>
        <div>Name</div>
        <div>Laker Net ID</div>
        <div>Role</div>
        <div></div> {/* Add an empty div for spacing */}
      </div>
      {userList.map((user) => (
        <div key={user.id} className='user-item'>
          <div>{user.name}</div>
          <div>{user.netID}</div>
          <div>{user.role}</div>
          <div>
            <button className='edit-button'>Edit</button>
            <button className='delete-button'>X</button>
          </div>
        </div>
      ))}
    </div>
  );
}

export default UserList;