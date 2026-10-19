Cypress.Commands.add('login', (overrides = {}) => {
  Cypress.log({
    name: 'loginViaAuth0',
  });

  const options = {
    method: 'POST',
    url: Cypress.env('auth_url'),
    body: {
      grant_type: 'password',
      username: Cypress.env('auth_username'),
      password: Cypress.env('auth_password'),
      audience: Cypress.env('auth_audience'),
      scope: 'openid profile email',
      client_id: Cypress.env('auth_client_id'),
      client_secret: Cypress.env('auth_client_secret'),
    },
  };
  cy.request(options);
});

// --- Custom command to simulate Auth0 login via localStorage ---
Cypress.Commands.add('loginByLocalStorage', () => {
  const fakeUser = {
    sub: 'auth0|testuserid',
    name: 'Test User',
    email: 'testuser@example.com',
    picture: '/images/placeholder-avatar.svg',
  };

  // Set up Auth0 SPA SDK cache
  window.localStorage.setItem(
    '@@auth0spajs@@::REDACTED_TOKEN::https://app.flexr.online/auth0/audience/api::openid profile email',
    JSON.stringify({
      body: {
        client_id: 'change-me-to-a-long-random-secret-of-at-least-32-bytes',
        access_token: 'change-me-to-a-long-random-secret-of-at-least-32-bytes',
        id_token: 'fake-id-token',
        scope: 'openid profile email',
        expires_in: 86400,
        token_type: 'Bearer',
        decodedToken: {
          user: fakeUser,
        }
      },
      expiresAt: Math.floor(Date.now() / 1000) + 86400,
    })
  );

  // Set user data in localStorage
  window.localStorage.setItem('user', JSON.stringify(fakeUser));
  
  // Set isAuthenticated flag
  window.localStorage.setItem('isAuthenticated', 'true');
});

describe('login', () => {
  it('should successfully log into our app', () => {
    cy.login()
      .then((resp) => {
        return resp.body;
      })
      .then((body) => {
        const {access_token, expires_in, id_token} = body;
        const auth0State = {
          nonce: '',
          state: 'some-random-state'
        };
        const callbackUrl = `/callback#access_token=${access_token}&scope=openid&id_token=${id_token}&expires_in=${expires_in}&token_type=Bearer&state=${auth0State.state}`;
        cy.visit(callbackUrl, {
          onBeforeLoad(win) {
            win.document.cookie = 'com.auth0.auth.some-random-state=' + JSON.stringify(auth0State);
          }
        });
      })
  });
});

describe('Create Post Feature', () => {
  beforeEach(() => {
    // Set up API mocks first
    cy.intercept('POST', '**/flex/post', {
      statusCode: 201,
      body: {
        id: 'test-post-id',
        content: 'This is a Cypress E2E test post!',
        authorId: 'auth0|testuserid',
        createdAt: new Date().toISOString()
      }
    }).as('createPost');

    cy.intercept('GET', '**/flex/post*', {
      statusCode: 200,
      body: []
    }).as('getPosts');

    // Visit home page and set up auth
    cy.visit('/', {
      onBeforeLoad: (win) => {
        // Signal to your app this is a Cypress test
        win.__CYPRESS__ = true;
        
        // Mock Auth0 state
        win.auth0 = {
          isAuthenticated: true,
          user: {
            sub: 'auth0|testuserid',
            name: 'Test User',
            email: 'testuser@example.com',
            picture: '/images/placeholder-avatar.svg',
          },
          getAccessTokenSilently: () => Promise.resolve('fake-access-token'),
        };
      }
    });

    // Set up auth in localStorage
    cy.loginByLocalStorage();

    // Reload to apply auth state
    cy.reload();
  });

  it('should create a new post with text only', () => {
    // Navigate to create post page
    cy.visit('/post/create', {
      onBeforeLoad: (win) => {
        win.__CYPRESS__ = true;
      }
    });

    // Verify we can see the create post form (not the login modal)
    cy.get('textarea#content', { timeout: 10000 })
      .should('be.visible')
      .should('not.be.disabled')
      .type('This is a Cypress E2E test post!');

    // Submit the form
    cy.get('form').submit();

    // Wait for API call and verify redirect
    cy.wait('@createPost');
    cy.url().should('eq', `${Cypress.config().baseUrl}/`);

    // Verify the post appears
    cy.contains('This is a Cypress E2E test post!').should('be.visible');
  });
});