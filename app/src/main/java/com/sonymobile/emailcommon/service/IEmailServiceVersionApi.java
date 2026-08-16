package com.sonymobile.emailcommon.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes2.dex */
public interface IEmailServiceVersionApi extends IInterface {
    public static final String DESCRIPTOR = "com.sonymobile.emailcommon.service.IEmailServiceVersionApi";

    public static class Default implements IEmailServiceVersionApi {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.sonymobile.emailcommon.service.IEmailServiceVersionApi
        public int getApiVersion() throws RemoteException {
            return 0;
        }
    }

    int getApiVersion() throws RemoteException;

    public static abstract class Stub extends Binder implements IEmailServiceVersionApi {
        static final int TRANSACTION_getApiVersion = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IEmailServiceVersionApi.DESCRIPTOR);
        }

        public static IEmailServiceVersionApi asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IEmailServiceVersionApi.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IEmailServiceVersionApi)) {
                return (IEmailServiceVersionApi) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IEmailServiceVersionApi.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IEmailServiceVersionApi.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                int apiVersion = getApiVersion();
                parcel2.writeNoException();
                parcel2.writeInt(apiVersion);
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IEmailServiceVersionApi {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IEmailServiceVersionApi.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.sonymobile.emailcommon.service.IEmailServiceVersionApi
            public int getApiVersion() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IEmailServiceVersionApi.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
