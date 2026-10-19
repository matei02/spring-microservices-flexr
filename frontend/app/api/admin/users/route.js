import { ManagementClient } from 'auth0';

const auth0 = new ManagementClient({
  domain: process.env.AUTH0_DOMAIN,
  clientId: process.env.AUTH0_MGMT_CLIENT_ID,
  clientSecret: process.env.AUTH0_MGMT_CLIENT_SECRET,
});

export async function GET() {
  try {
    const users = await auth0.getUsers();
    return Response.json(users);
  } catch (error) {
    console.error('Error fetching users:', error);
    return Response.json({ error: 'Failed to fetch users' }, { status: 500 });
  }
}

export async function PUT(request, { params }) {
  const { userId } = params;
  const { role } = await request.json();

  try {
    await auth0.assignRolestoUser({ id: userId }, { roles: [role] });
    return Response.json({ success: true });
  } catch (error) {
    console.error('Error updating user role:', error);
    return Response.json({ error: 'Failed to update user role' }, { status: 500 });
  }
}
