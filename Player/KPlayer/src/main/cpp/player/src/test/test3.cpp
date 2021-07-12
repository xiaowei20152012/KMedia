#include <iostream>
#include "test3.h"

using namespace std;

void my_test()
{
    my_texs1();
}

void my_texs1()
{

    int i, j, k, f;
    for (int i = 1; i <= 4; i++)
    {
        for (int j = 1; j <= 30; j++)
        {
            cout << " ";
        }
        for (int k = 1; k <= 8 - 2 * i; k++)
        {
            cout << " ";
        }
        for (f = 1; f <= 2 * i; f++)
        {
            cout << '*';
        }
        cout << endl;
    }

    for (i = 1; i <= 3; i++)
    {
        for (j = 1; j <= 30; j++)
        {
            cout << " ";
        }
        for (f = 1; f <= 7 - 2 * i; f++)
        {
            cout << '*';
        }
        cout << endl;
    }

}

