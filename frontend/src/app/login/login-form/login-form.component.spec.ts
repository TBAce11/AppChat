import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { TestHelper } from "src/app/test/test-helper";

import { LoginFormComponent } from "./login-form.component";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";

describe("LoginFormComponent", () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;
  let testHelper: TestHelper<LoginFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginFormComponent],
      imports: [
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    testHelper = new TestHelper(fixture);
    fixture.detectChanges();
  });

  it('should emit username and password', () => {
    let username: string;
    let password: string;

    // Subscribe to the EventEmitter to receive the emitted values.
    component.login.subscribe((event) => {
      username = event.username;
      password = event.password;
    });

    // Simulate user input
    const usernameInput = testHelper.getInput('username-input');
    const passwordInput = testHelper.getInput('password-input');
    testHelper.writeInInput(usernameInput, 'username');
    testHelper.writeInInput(passwordInput, 'pwd');

    // Trigger the login method
    component.onLogin();

    // Check if the emitted values match the expected values
    expect(username!).toBe('username');
    expect(password!).toBe('pwd');
    // Check if the form is valid after triggering login
    expect(component.loginForm.valid).toBe(true);
  });

  it('should not emit if username is not present', () => {
    let emitted = false;

    component.login.subscribe(() => {
      emitted = true;
    });

    // Simulate user input without providing a username
    const passwordInput = testHelper.getInput('password-input');
    testHelper.writeInInput(passwordInput, 'pwd');

    // Trigger the login method
    component.onLogin();

    // Check that no values were emitted
    expect(emitted).toBe(false);
    // Check for appropriate error message
    expect(component.errorMessage).toBe(''); //aucun message d'erreur 403
  });

  it('should not emit if password is not present', () => {
    let emitted = false;

    component.login.subscribe(() => {
      emitted = true;
    });

    // Simulate user input without providing a password
    const usernameInput = testHelper.getInput('username-input');
    testHelper.writeInInput(usernameInput, 'username');

    // Trigger the login method
    component.onLogin();

    // Check that no values were emitted
    expect(emitted).toBe(false);
    // Check for appropriate error message
    expect(component.errorMessage).toBe(''); 
  });

  it('should not emit if username and password are not present', () => {
    let emitted = false;

    component.login.subscribe(() => {
      emitted = true;
    });

    // Trigger the login method without providing username and password
    component.onLogin();

    // Check that no values were emitted
    expect(emitted).toBe(false);
    // Check for appropriate error messages
    expect(component.errorMessage).toBe(''); 
    expect(component.errorMessage).toBe(''); 
  });
});
