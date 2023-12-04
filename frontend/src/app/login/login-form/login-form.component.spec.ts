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

    component.login.subscribe((event) => {
      username = event.username;
      password = event.password;
    });

    const usernameInput = testHelper.getInput('username-input');
    const passwordInput = testHelper.getInput('password-input');
    testHelper.writeInInput(usernameInput, 'username');
    testHelper.writeInInput(passwordInput, 'pwd');

    component.onLogin();

    expect(username!).toBe('username');
    expect(password!).toBe('pwd');
    expect(component.loginForm.valid).toBe(true);
  });

  it('should not emit if username is not present', () => {
    let emitted = false;

    component.login.subscribe(() => {
      emitted = true;
    });

    const passwordInput = testHelper.getInput('password-input');
    testHelper.writeInInput(passwordInput, 'pwd');

    component.onLogin();

    expect(emitted).toBe(false);
    expect(component.errorMessage).toBe(''); //aucun message d'erreur 403
  });

  it('should not emit if password is not present', () => {
    let emitted = false;

    component.login.subscribe(() => {
      emitted = true;
    });

    const usernameInput = testHelper.getInput('username-input');
    testHelper.writeInInput(usernameInput, 'username');

    component.onLogin();

    expect(emitted).toBe(false);
    expect(component.errorMessage).toBe(''); 
  });

  it('should not emit if username and password are not present', () => {
    let emitted = false;

    component.login.subscribe(() => {
      emitted = true;
    });

    component.onLogin();

    expect(emitted).toBe(false);
    expect(component.errorMessage).toBe(''); 
    expect(component.errorMessage).toBe(''); 
  });
});
