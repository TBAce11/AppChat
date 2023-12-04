import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { chatPageGuard } from './chat-page.guard';

describe('chatPageGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => chatPageGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
